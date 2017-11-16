package org.someth2say.taijitu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.composite.CompositeComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.ui.config.source.FieldDescription;
import org.someth2say.taijitu.ui.config.source.Source;
import org.someth2say.taijitu.ui.config.source.mapper.SourceMapper;
import org.someth2say.taijitu.ui.registry.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

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
        List<ExtractorAndComparableCategorizerEquality<T, ?>> categoryEaEs;
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
            logger.debug("Category EaEs for {}:\n {}", sd.source.getName(), IntStream.range(0, identityFields.size()).mapToObj(fdIdx -> sd.categoryEaEs.get(fdIdx).toString() + "(" + identityFields.get(fdIdx).toString() + ")").collect(Collectors.joining(",\n ")));
            sd.equalityEaEs = nonIdentityFields.stream().map(fd -> buildExtractorAndEquality(iComparisonCfg, sd, fd)).collect(Collectors.toList());
            logger.debug("Equality EaEs for {}:\n {}", sd.source.getName(), IntStream.range(0, nonIdentityFields.size()).mapToObj(fdIdx -> sd.equalityEaEs.get(fdIdx).toString() + "(" + nonIdentityFields.get(fdIdx).toString() + ")").collect(Collectors.joining(",\n ")));
        });

        //4. Build CompositeEqualities with ValueExtractors and ValueEqualities
        //TODO: This assumes all sources use the same extractors! Else, we need an "HybridCompositeEquality", providing, for each field on a) extractors for each source, and b) valueEqualities
        SourceData<?, T> tSourceData = sourceDatas.get(0);
        ComparableCategorizerEquality<T> categorizer = new CompositeComparableCategorizerEquality<>(tSourceData.categoryEaEs);
        Equality<T> equality = new CompositeEquality<>(tSourceData.equalityEaEs);

        //6. Run SteamEquality given CategorizerEquality and MappedStreams
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
        Equality<V> equality = getEquality(fd, iComparisonCfg);
        return new ExtractorAndEquality<>(extractor, equality);
    }

    private <T, V> ExtractorAndComparableCategorizerEquality<T, V> buildExtractorAndComparableEquality(IComparisonCfg iComparisonCfg, SourceData<?, T> sd, FieldDescription<V> fd) {
        Function<T, V> extractor = sd.mappedSource.getExtractor(fd);
        if (extractor == null) {
            throw new RuntimeException("Can't obtain extractor for field " + fd);
        }
        ComparableCategorizerEquality<V> valueEquality = getComparableEquality(fd, iComparisonCfg);
        return new ExtractorAndComparableCategorizerEquality<>(extractor, valueEquality);
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

        if (commonClass != sourceMappedClass) {
            if (commonClass == null) {
                logger.debug("Setting sources common class to {} due to {} ", sourceMappedClass, sd.source.getName());
                commonClass = sourceMappedClass;

            } else if (sourceMappedClass.isAssignableFrom(commonClass)) {
                logger.debug("Upcasting sources common class to {} due to {}", sourceMappedClass, sd.source.getName());
                commonClass = sourceMappedClass;
            } else {
                logger.error("Unable to find a common class for all sources! Was {}, but source {} generates {}", commonClass, sd.source.getName(), sourceMappedClass);
                throw new RuntimeException("Unable to find a common class for all sources!");
            }
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
        Source<R> source = sourceData.source;
        if (mapperName != null) {
            SourceMapper<R, T> mapper = MapperRegistry.getInstance(mapperName);
            if (mapper == null) {
                throw new RuntimeException("Can't find mapper instance: " + mapperName + " for source " + source.getName());
            }
            sourceData.mapper = mapper;
        } else {
            logger.trace("Source {} have no mapper defined, so will directly generate composite type {}", source.getName(), source.getTypeParameter().getName());
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

    private <V> Equality<V> getEquality(FieldDescription<V> fd, IComparisonCfg iComparisonCfg) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd.getClazz(), fd.getName(), iComparisonCfg.getEqualityConfigs());
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
    }


    private <V> ComparableCategorizerEquality<V> getComparableEquality(FieldDescription<V> fd, IComparisonCfg iComparisonCfg) {
        List<IEqualityCfg> iEqualityCfgs = getEqualityConfigsFor(fd.getClazz(), fd.getName(), iComparisonCfg.getEqualityConfigs());
        Optional<ComparableCategorizerEquality<V>> first = iEqualityCfgs.stream().map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters())).filter(eq -> eq instanceof ComparableCategorizerEquality).map(eq -> (ComparableCategorizerEquality<V>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        throw new RuntimeException("Can't find any comparable categorizer equality for field " + fd);

    }


    private List<IEqualityCfg> getEqualityConfigsFor(final String fieldClass, final String fieldName, final List<IEqualityCfg> equalityCfgs) {
        List<IEqualityCfg> perfectMatchesEqualities = equalityCfgs.stream().filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).collect(Collectors.toList());
        if (!perfectMatchesEqualities.isEmpty()) return perfectMatchesEqualities;
        List<IEqualityCfg> byNameEqualities = equalityCfgs.stream().filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).collect(Collectors.toList());
        if (!byNameEqualities.isEmpty()) return byNameEqualities;
        List<IEqualityCfg> byClassEqualities = equalityCfgs.stream().filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).collect(Collectors.toList());
        if (!byClassEqualities.isEmpty()) return byClassEqualities;
        List<IEqualityCfg> anythingMatchEqualities = equalityCfgs.stream().filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).collect(Collectors.toList());
        return anythingMatchEqualities;
    }


    private IEqualityCfg getEqualityConfigFor(final String fieldClass, final String fieldName, final List<IEqualityCfg> equalityConfigIfaces) {

        Optional<IEqualityCfg> perfectMatchesEqualities = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> byNameEqualities = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).findFirst();
        Optional<IEqualityCfg> byClassEqualities = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> anythingMatchEqualities = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).findFirst();

        return perfectMatchesEqualities.orElse(byNameEqualities.orElse(byClassEqualities.orElse(anythingMatchEqualities.orElse(null))));
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
