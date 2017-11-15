package org.someth2say.taijitu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.composite.ComparableCompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndComparableEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.value.AbstractValueEquality;
import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.registry.*;
import org.someth2say.taijitu.source.FieldDescription;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.source.mapper.SourceMapper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jordi Sola
 */
class TaijituRunner implements Callable<ComparisonResult> {

    private static final Logger logger = LoggerFactory.getLogger(TaijituRunner.class);

    private final IComparisonCfg comparisonCfg;

    public TaijituRunner(final IComparisonCfg comparisonCfg) {
        this.comparisonCfg = comparisonCfg;
    }

    @Override
    public ComparisonResult call() {
        List<IPluginCfg> pluginConfigs = comparisonCfg.getPluginConfigs();

        runPluginsPreComparison(pluginConfigs, comparisonCfg);

        ComparisonResult result = runComparison(comparisonCfg);

        runPluginsPostComparison(pluginConfigs, comparisonCfg);

        return result;
    }

    private void runPluginsPostComparison(List<IPluginCfg> plugins, IComparisonCfg comparisonConfig) {
        for (IPluginCfg plugin : plugins) {
            PluginRegistry.getPlugin(plugin.getName()).postComparison(plugin, comparisonConfig);
        }
    }

    private void runPluginsPreComparison(List<IPluginCfg> plugins, IComparisonCfg comparisonCfg) {
        for (IPluginCfg plugin : plugins) {
            PluginRegistry.getPlugin(plugin.getName()).preComparison(plugin, comparisonCfg);
        }
    }

    private class SourceData<R, T> {
        final ISourceCfg sourceCfg;
        Source<R> source;
        Source<T> mappedSource;
        SourceMapper<R, T> mapper;
        List<ExtractorAndComparableEquality<T, ?>> categoryEaEs;
        List<ExtractorAndEquality<T, ?>> equalityEaEs;

        private SourceData(ISourceCfg sourceCfg) {
            this.sourceCfg = sourceCfg;
        }
    }

    private <T> ComparisonResult<T> runComparison(IComparisonCfg iComparisonCfg) {

        List<ISourceCfg> sourceConfigs = iComparisonCfg.getSourceConfigs();
        if (sourceConfigs.size() < 2) {
            throw new RuntimeException("There should be at least 2 sources configured, but only " + sourceConfigs.size() + " found");
        }
        if (sourceConfigs.size() > 2) {
            logger.warn("More than 2 sources found ({}}). Only first two will be considered!", sourceConfigs.size());
        }

        // XD SourceData<T,T>... T.T Hate type erasure...
        List<SourceData<?, T>> sourceDatas = sourceConfigs.stream().limit(2).map(SourceData<T, T>::new).collect(Collectors.toList());

        //0. Build and map sources
        buildMappedSources(sourceDatas);

        //2. Calculate common fields (gives all sources)
        List<FieldDescription<?>> commonFields = getCommonFields(sourceDatas.stream().map(sd -> sd.mappedSource).collect(Collectors.toList()));

        //A. Get fields for each comparison:
        //A.1.- Identity
        List<FieldDescription<?>> identityFields = commonFields.stream().filter(fd -> iComparisonCfg.getKeyFields().contains(fd.getName())).collect(Collectors.toList());
        //A.2.- CategoryEquality (for mapping) and ComparableEquality (for sorted)
        //TODO: Currently we have no difference between category and sorting fields. So we just use all non-key fields
        List<FieldDescription<?>> nonIdentityFields = commonFields.stream().filter(fd -> !identityFields.contains(fd)).collect(Collectors.toList());

        //3. Obtain ValueExtractors and Equalities
        sourceDatas.forEach((SourceData<?, T> sd) -> {
            sd.categoryEaEs = identityFields.stream().map(fd -> buildExtractorAndComparableEquality(iComparisonCfg, sd, fd)).collect(Collectors.toList());
            sd.equalityEaEs = nonIdentityFields.stream().map(fd -> buildExtractorAndEquality(iComparisonCfg, sd, fd)).collect(Collectors.toList());
        });

        //4. Build CompositeEqualities with ValueExtractors and ValueEqualities
        //TODO: This assumes all sources use the same extractors! Else, we need an "HybridCompositeEquality", providing, for each field on a) extractors for each source, and b) valueEqualities
        ICompositeEquality<T> categorizer = new ComparableCompositeEquality<>(sourceDatas.get(0).categoryEaEs);
        ICompositeEquality<T> equality = new CompositeEquality<>(sourceDatas.get(0).equalityEaEs);

        //6. Run SteamEquality given ICompositeEquality and MappedStreams
        String strategyName = iComparisonCfg.getStrategyConfig().getName();
        final StreamEquality<T> streamEquality = StreamEqualityRegistry.getInstance(strategyName, equality, categorizer);

        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        logger.info("Comparison {} ready to run.", iComparisonCfg.getName());
        return runStreamEquality(streamEquality, sourceDatas);
    }

    private <T, V> ExtractorAndEquality<T, V> buildExtractorAndEquality(IComparisonCfg iComparisonCfg, SourceData<?, T> sd, FieldDescription<V> fd) {
        Function<T, V> extractor = sd.mappedSource.getExtractor(fd);
        if (extractor == null) {
            throw new RuntimeException("Can't obtain extractor for field " + fd);
        }
        ValueEquality<V> valueEquality = getEquality(fd, iComparisonCfg);
        return new ExtractorAndEquality<>(extractor, valueEquality);
    }

    private <T, V> ExtractorAndComparableEquality<T, V> buildExtractorAndComparableEquality(IComparisonCfg iComparisonCfg, SourceData<?, T> sd, FieldDescription<V> fd) {
        Function<T, V> extractor = sd.mappedSource.getExtractor(fd);
        if (extractor == null) {
            throw new RuntimeException("Can't obtain extractor for field " + fd);
        }
        ComparableValueEquality<V> valueEquality = getComparableEquality(fd, iComparisonCfg);
        return new ExtractorAndComparableEquality<>(extractor, valueEquality);
    }


    private <T> void buildMappedSources(List<SourceData<?, T>> sourceDatas) {
        buildSources(sourceDatas);

        buildMappers(sourceDatas);

        Class<T> commonClass = checkCommonGeneratedClass(sourceDatas);

        mapSources(sourceDatas, commonClass);
    }

    private <T> void mapSources(List<SourceData<?, T>> sourceDatas, Class<T> commonClass) {
        for (SourceData<?, T> sourceData : sourceDatas) {
            mapSource(sourceData, commonClass);
        }
    }

    private <R, T> void mapSource(SourceData<R, T> sourceData, Class<T> commonClass) {
        SourceMapper<R, T> mapper = sourceData.mapper;
        if (mapper == null) {
            Class<R> sourceTypeParameter = sourceData.source.getTypeParameter();
            if (commonClass.isAssignableFrom(sourceTypeParameter)) {
                logger.debug("Source {} have no mapper defined, so will directly generate composite type {}", sourceData.source.getName(), sourceTypeParameter.getName());
                //TODO: What should we do with this unchecked cast?
                sourceData.mappedSource = (Source<T>) sourceData.source;
            } else {
                throw new RuntimeException("Source " + sourceData.source.getName() + " generate incompatible class " + sourceTypeParameter.getName() + " (need " + commonClass.getName() + ")");
            }
        } else {
            logger.debug("Applying mapper {} to source {} to produce composite type {}", mapper.getName(), sourceData.source.getName(), mapper.getTypeParameter().getSimpleName());
            sourceData.mappedSource = mapper.apply(sourceData.source);
        }
    }

    private <T> Class<T> checkCommonGeneratedClass(List<SourceData<?, T>> sourceDatas) {
        Class<T> expectedClass = null;
        for (SourceData<?, T> sd : sourceDatas) {
            expectedClass = checkGeneratedClass(expectedClass, sd);
        }
        return expectedClass;
    }

    private <R, T> Class<T> checkGeneratedClass(Class<T> commonClass, SourceData<R, T> sd) {
        SourceMapper<R, T> mapper = sd.mapper;

        Class<R> sourceTypeParameter = sd.source.getTypeParameter();

        //TODO: What should we do with this unchecked cast?
        Class<T> sourceMappedClass = mapper != null ? mapper.getTypeParameter() : (Class<T>) sourceTypeParameter;

        if ((commonClass == null) || sourceMappedClass.isAssignableFrom(commonClass)) {
            commonClass = sourceMappedClass;
        } else {
            throw new RuntimeException("Unable to find a common class for all sources!");
        }

        return commonClass;
    }

    private <T> void buildMappers(List<SourceData<?, T>> sourceDatas) {
        for (SourceData<?, T> sourceData : sourceDatas) {
            buildMapper(sourceData);
        }
    }

    private <R, T> void buildMapper(SourceData<R, T> sourceData) {
        String mapperName = sourceData.sourceCfg.getMapper();
        if (mapperName != null) {
            SourceMapper<R, T> mapper = MapperRegistry.getInstance(mapperName);
            if (mapper == null) {
                throw new RuntimeException("Can't find mapper instance: " + mapperName + " for source " + sourceData.source.getName());
            }
            sourceData.mapper = mapper;
        } else {
            sourceData.mapper = null;
        }
    }

    private <T> void buildSources(List<SourceData<?, T>> sourceDatas) {
        for (SourceData<?, T> sourceData : sourceDatas) {
            buildSource(sourceData);
        }
    }

    private <R, T> void buildSource(SourceData<R, T> sourceData) {
        Source<R> source = SourceRegistry.getInstance(sourceData.sourceCfg.getType(), sourceData.sourceCfg);
        if (source == null) {
            throw new RuntimeException("Can't find source type " + sourceData.sourceCfg.getType());
        }
        sourceData.source = source;
    }

    private <T> ComparisonResult<T> runStreamEquality(StreamEquality<T> streamEquality, List<SourceData<?, T>> sourceDatas) {
        ComparisonResult<T> comparisonResult = null;
        try (Stream<T> sourceStr = sourceDatas.get(0).mappedSource.stream();
             Stream<T> targetStr = sourceDatas.get(1).mappedSource.stream();
             Source sourceSrc = sourceDatas.get(0).mappedSource;
             Source targetSrc = sourceDatas.get(1).mappedSource) {
            comparisonResult = streamEquality.runComparison(sourceStr, sourceSrc.getName(), targetStr, targetSrc.getName());
            return comparisonResult;
        } catch (Source.ClosingException e) {
            //TODO: We are actually closing the stream, not the source!!!!!
            logger.warn("Unable to close sources.", e);
            // Misleading warning. See https://youtrack.jetbrains.com/issue/IDEA-181860
            return comparisonResult;
        }

    }

    private <V> ValueEquality<V> getEquality(FieldDescription<V> fd, IComparisonCfg iComparisonCfg) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd.getClazz(), fd.getName(), iComparisonCfg.getEqualityConfigs());
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
    }


    private <V> ComparableValueEquality<V> getComparableEquality(FieldDescription<V> fd, IComparisonCfg iComparisonCfg) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd.getClazz(), fd.getName(), iComparisonCfg.getEqualityConfigs());
        AbstractValueEquality<V> abstractValueEquality = ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
        //TODO: registry may be able to determine comparable equalities.
        return (ComparableValueEquality<V>) abstractValueEquality;
    }

    private IEqualityCfg getEqualityConfigFor(final String fieldClass, final String fieldName, final List<IEqualityCfg> equalityConfigIfaces) {

        Optional<IEqualityCfg> perfectMatches = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> nameMatches = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).findFirst();
        Optional<IEqualityCfg> classMathes = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> allMathes = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).findFirst();

        return perfectMatches.orElse(nameMatches.orElse(classMathes.orElse(allMathes.orElse(null))));
    }

    private boolean fieldNameMatch(String fieldName, IEqualityCfg eq) {
        return eq.getFieldName() != null && fieldName.equals(eq.getFieldName());
    }

    private boolean fieldClassMatch(String fieldClassName, IEqualityCfg eq) {
        if (fieldClassName == null) return false;
        String configClassName = eq.getFieldClass();
        if (configClassName == null) return false;
        if (eq.isFieldClassStrict()) {
            return fieldClassName.equals(configClassName);
        } else {
            try {
                Class<?> configClass = Class.forName(configClassName);
                Class<?> fieldClass = Class.forName(fieldClassName);
                return configClass.isAssignableFrom(fieldClass);
            } catch (ClassNotFoundException e) {
                logger.error("Class defined in equality config not found:" + configClassName, e);
                return false;
            }
        }
    }

    private <T> List<FieldDescription<?>> getCommonFields(List<Source<T>> sources) {
        List<FieldDescription<?>> canonicalFDs = sources.get(0).getProvidedFields();
        // Retain only canonicalFields that are also provided by other streams.
        for (Source<T> source : sources.stream().skip(1).collect(Collectors.toList())) {
            List<FieldDescription<?>> sourceFDs = source.getProvidedFields();
            canonicalFDs.retainAll(sourceFDs);
        }
        return canonicalFDs;
    }


}
