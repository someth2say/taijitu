package org.someth2say.taijitu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.external.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.external.ComparatorEquality;
import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.composite.CompositeCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeComparatorEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.simple.SimpleStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.ui.registry.*;
import org.someth2say.taijitu.ui.source.FieldDescription;
import org.someth2say.taijitu.ui.source.Source;
import org.someth2say.taijitu.ui.source.mapper.SourceMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Jordi Sola
 */
class TaijituRunner implements Callable<List<Mismatch>> {

    private static final Logger logger = LoggerFactory.getLogger(TaijituRunner.class);

    private final IComparisonCfg comparisonCfg;

    public TaijituRunner(final IComparisonCfg comparisonCfg) {
        this.comparisonCfg = comparisonCfg;
    }

    @Override
    public List<Mismatch> call() {
        List<IPluginCfg> pluginConfigs = comparisonCfg.getPluginConfigs();

        runPluginsPreComparison(pluginConfigs, comparisonCfg);

        List<Mismatch> result = runComparison(comparisonCfg);

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

        private SourceData(ISourceCfg sourceCfg) {
            this.sourceCfg = sourceCfg;
        }
    }

    private <T> List<Mismatch> runComparison(IComparisonCfg iComparisonCfg) {

        List<ISourceCfg> sourceConfigs = iComparisonCfg.getSourceConfigs();
        if (sourceConfigs.size() < 2) {
            throw new RuntimeException(
                    "There should be at least 2 sources configured, but only " + sourceConfigs.size() + " found");
        }
        if (sourceConfigs.size() > 2) {
            logger.warn("More than 2 sources found ({}}). Only first two will be considered!", sourceConfigs.size());
        }

        // XD SourceData<T,T>... T.T Hate type erasure...
        List<SourceData<?, T>> sourceDatas = sourceConfigs.stream().limit(2).map(SourceData<T, T>::new)
                .collect(Collectors.toList());

        // 0. Build and map sources
        buildMappedSources(sourceDatas);

        // 2. Calculate common fields (gives all sources)
        Map<String, FieldDescription<?>> commonFDs = getCommonFields(sourceDatas.stream().map(sd -> sd.mappedSource).collect(Collectors.toList()));

        // A. Get fields for each comparison:
        // A.1.- Identity
//        List<FieldDescription<?>> identityFDs = commonFDs.stream().filter(fd -> iComparisonCfg.getKeyFields().contains(fd.getName())).collect(Collectors.toList());
        // A.2.- CategoryEquality (for mapping) and ComparatorEquality (for sorted) we just use all non-key fields
//        List<FieldDescription<?>> nonIdentityFDs = commonFDs.stream().filter(fd -> !identityFDs.contains(fd)).collect(Collectors.toList());

        // 3.- Create comparators:
        // 3.1.- If Key fields present, build categorizer
        CompositeCategorizerEquality<T> categorizer = null;
        List<FieldDescription<?>> keyFDs = iComparisonCfg.getKeyFields().stream().map(commonFDs::get).collect(Collectors.toList());
        if (!keyFDs.isEmpty()) {
            CompositeCategorizerEquality.Builder<T> categorizerBuilder = new CompositeCategorizerEquality.Builder<>();
            keyFDs.forEach(fd -> addCategorizerComponent(iComparisonCfg, sourceDatas, categorizerBuilder, fd));
            categorizer = categorizerBuilder.build();
        }

        // 3.2.- If Sort fields present, build comparator
        CompositeComparatorEquality<T> sorter = null;
        List<FieldDescription<?>> sortFDs = iComparisonCfg.getSortFields().stream().map(commonFDs::get).collect(Collectors.toList());
        if (!sortFDs.isEmpty()) {
            CompositeComparatorEquality.Builder<T> comparerBuilder = new CompositeComparatorEquality.Builder<>();
            sortFDs.forEach(fd -> addComparerComponent(iComparisonCfg, sourceDatas, comparerBuilder, fd));
            sorter = comparerBuilder.build();
        }

        // 3.3.a.- If Compare fields present, build equality
        // 3.3.b.- If no compare fields, use fields not key nor sort.
        CompositeEquality<T> equality = null;
        List<String> compareFDS = iComparisonCfg.getCompareFields();
        List<FieldDescription<?>> compareFDs =
                (!compareFDS.isEmpty()
                        ? commonFDs.values().stream().filter(commonFD -> compareFDS.contains(commonFD.getName())) //TODO: Check that all compare fields actually are provided
                        : commonFDs.values().stream().filter(commonFD -> !keyFDs.contains(commonFD) && !sortFDs.contains(commonFD))).collect(Collectors.toList());
        if (!compareFDs.isEmpty()) {
            CompositeEquality.Builder<T> equalityBuilder = new CompositeEquality.Builder<>();
            compareFDs.forEach(fd -> addComponent(iComparisonCfg, sourceDatas, equalityBuilder, fd));
            equality = equalityBuilder.build();
        }

        // 6. Run SteamEquality given CategorizerEquality and MappedStreams
        final StreamEquality<T> streamEquality;
        if (equality == null) {
            throw new RuntimeException("Unable to define comparison fields!");
        } else {
            if (sorter == null && categorizer == null) {
                throw new RuntimeException("Hybrid equality not supported yet");
            } else {
                if (sorter == null) {
                    streamEquality = new MappingStreamEquality<T>(equality, categorizer);
                } else if (categorizer == null) {
                    streamEquality = new ComparableStreamEquality<T>(equality, sorter);
                } else {
                    streamEquality = new SimpleStreamEquality<>(equality);
                }
            }
        }

        // Shall we use the same matcher for canonical source? No, we should use
        // identity matcher....
        logger.info("Comparison {} ready to run.", iComparisonCfg.getName());
        return runStreamEquality(streamEquality, sourceDatas);
    }

    private <T, V> void addComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeEquality.Builder<T> equalityBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        Equality<V> vEquality = getEquality(fd, iComparisonCfg.getEqualityConfigs());
        equalityBuilder.addComponent(extractor, vEquality);
    }

    private <T, V> void addCategorizerComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeCategorizerEquality.Builder<T> categorizerBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        CategorizerEquality<V> vEquality = getCategorizerEquality(fd, iComparisonCfg.getEqualityConfigs());
        categorizerBuilder.addComponent(extractor, vEquality);
    }

    private <T, V> void addComparerComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeComparatorEquality.Builder<T> comparerBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        ComparatorEquality<V> vEquality = getComparableEquality(fd, iComparisonCfg.getEqualityConfigs());
        comparerBuilder.addComponent(extractor, vEquality);
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
                // TODO: What should we do with this unchecked cast?
                sourceData.mappedSource = (Source<T>) sourceData.source;
            } else {
                throw new RuntimeException("Source " + sourceData.source.getName() + " generate incompatible class "
                        + sourceTypeParameter.getName() + " (need " + commonClass.getName() + ")");
            }
        } else {
            logger.debug("Applying mapper {} to source {} to produce composite type {}", mapper.getName(),
                    sourceData.source.getName(), mapper.getTypeParameter().getSimpleName());
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

        // TODO: What should we do with this unchecked cast?
        Class<T> sourceMappedClass = mapper != null ? mapper.getTypeParameter() : (Class<T>) sourceTypeParameter;

        if (commonClass != sourceMappedClass) {
            if (commonClass == null) {
                logger.debug("Setting sources common class to {} due to {} ", sourceMappedClass, sd.source.getName());
                commonClass = sourceMappedClass;

            } else if (sourceMappedClass.isAssignableFrom(commonClass)) {
                logger.debug("Upcasting sources common class to {} due to {}", sourceMappedClass, sd.source.getName());
                commonClass = sourceMappedClass;
            } else {
                logger.error("Unable to find a common class for all sources! Was {}, but source {} generates {}",
                        commonClass, sd.source.getName(), sourceMappedClass);
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
                throw new RuntimeException(
                        "Can't find mapper instance: " + mapperName + " for source " + source.getName());
            }
            sourceData.mapper = mapper;
        } else {
            logger.trace("Source {} have no mapper defined, so will directly generate composite type {}",
                    source.getName(), source.getTypeParameter().getName());
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

    private <T> List<Mismatch> runStreamEquality(StreamEquality<T> streamEquality, List<SourceData<?, T>> sourceDatas) {
        List<Mismatch> comparisonResult = null;
        SourceData<?, T> sourceData0 = sourceDatas.get(0);
        SourceData<?, T> sourceData1 = sourceDatas.get(1);
        try (Source sourceSrc = sourceData0.mappedSource; Source targetSrc = sourceData1.mappedSource) {
            comparisonResult = streamEquality.underlyingDiffs(sourceSrc.stream(), targetSrc.stream());
            return comparisonResult;
        } catch (Source.ClosingException e) {
            logger.warn("Unable to close sources.", e);
            // Misleading warning in Intellij. See https://youtrack.jetbrains.com/issue/IDEA-181860
            return comparisonResult;
        }
    }

    private <V> Equality<V> getEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd, equalityConfigs);
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
    }

    private <V> ComparatorEquality<V> getComparableEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<ComparatorEquality<V>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof ComparatorEquality)
                .map(eq -> (ComparatorEquality<V>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        throw new RuntimeException("Can't find any comparable equality for field " + fd);
    }

    private <V> CategorizerEquality<V> getCategorizerEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<CategorizerEquality<V>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof CategorizerEquality)
                .map(eq -> (CategorizerEquality<V>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        throw new RuntimeException("Can't find any categorizer equality for field " + fd);
    }

    private List<IEqualityCfg> getEqualityConfigsFor(FieldDescription fd, final List<IEqualityCfg> equalityCfgs) {
        final String fieldClass = fd.getClazz();
        final String fieldName = fd.getName();
        List<IEqualityCfg> perfectMatchesEqualities = equalityCfgs.stream()
                .filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq))
                .collect(Collectors.toList());
        if (!perfectMatchesEqualities.isEmpty())
            return perfectMatchesEqualities;
        List<IEqualityCfg> byNameEqualities = equalityCfgs.stream()
                .filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).collect(Collectors.toList());
        if (!byNameEqualities.isEmpty())
            return byNameEqualities;
        List<IEqualityCfg> byClassEqualities = equalityCfgs.stream()
                .filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq))
                .collect(Collectors.toList());
        if (!byClassEqualities.isEmpty())
            return byClassEqualities;
        List<IEqualityCfg> anythingMatchEqualities = equalityCfgs.stream()
                .filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).collect(Collectors.toList());
        return anythingMatchEqualities;
    }

    private IEqualityCfg getEqualityConfigFor(FieldDescription fieldDescription, final List<IEqualityCfg> equalityConfigIfaces) {
        final String fieldClass = fieldDescription.getClazz();
        final String fieldName = fieldDescription.getName();
        Optional<IEqualityCfg> perfectMatchesEqualities = equalityConfigIfaces.stream()
                .filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> byNameEqualities = equalityConfigIfaces.stream()
                .filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).findFirst();
        Optional<IEqualityCfg> byClassEqualities = equalityConfigIfaces.stream()
                .filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> anythingMatchEqualities = equalityConfigIfaces.stream()
                .filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).findFirst();

        return perfectMatchesEqualities
                .orElse(byNameEqualities.orElse(byClassEqualities.orElse(anythingMatchEqualities.orElse(null))));
    }

    private boolean fieldNameMatch(String fieldName, IEqualityCfg eq) {
        return eq.getFieldName() != null && fieldName.equals(eq.getFieldName());
    }

    private boolean fieldClassMatch(String fieldClassName, IEqualityCfg eq) {
        if (fieldClassName == null)
            return false;
        String configClassName = eq.getFieldClass();
        if (configClassName == null)
            return false;
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

    private <T> Map<String, FieldDescription<?>> getCommonFields(List<Source<T>> sources) {
        return getCanonicalFDs(sources).stream().collect(Collectors.toMap(FieldDescription::getName, Function.identity()));
    }

    private <T> List<FieldDescription<?>> getCanonicalFDs(List<Source<T>> sources) {
        List<FieldDescription<?>> canonicalFDs = sources.get(0).getProvidedFields();
        // Retain only canonicalFields that are also provided by other streams.
        for (Source<T> source : sources.stream().skip(1).collect(Collectors.toList())) {
            List<FieldDescription<?>> sourceFDs = source.getProvidedFields();
            canonicalFDs.retainAll(sourceFDs);
        }
        return canonicalFDs;
    }

}
