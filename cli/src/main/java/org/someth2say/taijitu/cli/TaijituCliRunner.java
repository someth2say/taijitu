package org.someth2say.taijitu.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeComparator;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeHasher;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.simple.SimpleStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.sorted.ComparableStreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.cli.registry.*;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.mapper.SourceMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jordi Sola
 */
class TaijituCliRunner implements Callable<Stream<Difference<?>>> {

    private static final Logger logger = LoggerFactory.getLogger(TaijituCliRunner.class);

    private final IComparisonCfg comparisonCfg;

    public TaijituCliRunner(final IComparisonCfg comparisonCfg) {
        this.comparisonCfg = comparisonCfg;
    }

    @Override
    public Stream<Difference<?>> call() {
        List<IPluginCfg> pluginConfigs = comparisonCfg.getPluginConfigs();

        runPluginsPreComparison(pluginConfigs, comparisonCfg);

        Stream<Difference<?>> result = runComparison(comparisonCfg);

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

    private <T> Stream<Difference<?>> runComparison(IComparisonCfg iComparisonCfg) {

        List<ISourceCfg> sourceConfigs = iComparisonCfg.getSourceConfigs();
        if (sourceConfigs.size() < 2) {
            throw new RuntimeException(
                    "There should be at least 2 sources configured, but only " + sourceConfigs.size() + " found");
        }
        if (sourceConfigs.size() > 2) {
            logger.warn("More than 2 sources found ({}}). Only first two will be considered!", sourceConfigs.size());
        }

        // 0. Build and map sources
        List<SourceData<?, T>> sourceDatas = buildSourceDatas(sourceConfigs);

        // 2. Calculate common fields (gives all sources)
        Map<String, FieldDescription<?>> commonFDs = getCommonFields(sourceDatas.stream().map(sd -> sd.mappedSource).collect(Collectors.toList()));
        final StreamEqualizer<T> streamEquality = getStreamEqualizer(iComparisonCfg, sourceDatas, commonFDs);


        // Shall we use the same matcher for canonical source? No, we should use
        // identity matcher....
        logger.info("Comparison {} ready to run.", iComparisonCfg.getName());
        return runStreamEquality(streamEquality, sourceDatas);
    }

    private <T> StreamEqualizer<T> getStreamEqualizer(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, Map<String, FieldDescription<?>> commonFDs) {
        // 3.- Create comparators:
        // 3.1.- If Key fields present, build categorizer
        CompositeHasher<T> categorizer = null;
        List<FieldDescription<?>> keyFDs = iComparisonCfg.getKeyFields().stream().map(commonFDs::get).collect(Collectors.toList());
        if (!keyFDs.isEmpty()) {
            CompositeHasher.Builder<T> categorizerBuilder = new CompositeHasher.Builder<>();
            keyFDs.forEach(fd -> addCategorizerComponent(iComparisonCfg, sourceDatas, categorizerBuilder, fd));
            categorizer = categorizerBuilder.build();
        }

        // 3.2.- If Sort fields present, build comparator
        CompositeComparator<T> sorter = null;
        List<FieldDescription<?>> sortFDs = iComparisonCfg.getSortFields().stream().map(commonFDs::get).collect(Collectors.toList());
        if (!sortFDs.isEmpty()) {
            CompositeComparator.Builder<T> comparerBuilder = new CompositeComparator.Builder<>();
            sortFDs.forEach(fd -> addComparerComponent(iComparisonCfg, sourceDatas, comparerBuilder, fd));
            sorter = comparerBuilder.build();
        }

        // 3.3.a.- If Compare fields present, build equality
        // 3.3.b.- If no compare fields, use fields not key nor sort.
        CompositeEqualizer<T> equality = null;
        List<String> compareFDS = iComparisonCfg.getCompareFields();
        List<FieldDescription<?>> compareFDs =
                (!compareFDS.isEmpty()
                        ? commonFDs.values().stream().filter(commonFD -> compareFDS.contains(commonFD.getName())) //TODO: Check that all compare fields actually are provided
                        : commonFDs.values().stream().filter(commonFD -> !keyFDs.contains(commonFD) && !sortFDs.contains(commonFD))).collect(Collectors.toList());
        if (!compareFDs.isEmpty()) {
            CompositeEqualizer.Builder<T> equalityBuilder = new CompositeEqualizer.Builder<>();
            compareFDs.forEach(fd -> addComponent(iComparisonCfg, sourceDatas, equalityBuilder, fd));
            equality = equalityBuilder.build();
        }

        // 6. Run SteamEquality given Hasher and MappedStreams
        final StreamEqualizer<T> streamEquality;
        if (equality == null) {
            throw new RuntimeException("Unable to define comparison fields!");
        } else {
            if (sorter == null && categorizer == null) {
                throw new RuntimeException("Hybrid equality not supported yet");
            } else {
                if (sorter == null) {
                    streamEquality = new HashingStreamEqualizer<>(equality, categorizer);
                } else if (categorizer == null) {
                    streamEquality = new ComparableStreamEqualizer<>(equality, sorter);
                } else {
                    streamEquality = new SimpleStreamEqualizer<>(equality);
                }
            }
        }
        return streamEquality;
    }

    private <T, V> void addComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeEqualizer.Builder<T> equalityBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        Equalizer<V> vEqualizer = getEquality(fd, iComparisonCfg.getEqualityConfigs());
        equalityBuilder.addComponent(extractor, vEqualizer);
    }

    private <T, V> void addCategorizerComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeHasher.Builder<T> categorizerBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        Hasher<V> vEquality = getCategorizerEquality(fd, iComparisonCfg.getEqualityConfigs());
        categorizerBuilder.addComponent(extractor, vEquality);
    }

    private <T, V> void addComparerComponent(IComparisonCfg iComparisonCfg, List<SourceData<?, T>> sourceDatas, CompositeComparator.Builder<T> comparerBuilder, FieldDescription<V> fd) {
        Source<T> mappedSource = sourceDatas.get(0).mappedSource;
        Function<T, V> extractor = mappedSource.getExtractor(fd);
        Comparator<V> vEquality = getComparableEquality(fd, iComparisonCfg.getEqualityConfigs());
        comparerBuilder.addComponent(extractor, vEquality);
    }

    private <T> List<SourceData<?, T>> buildSourceDatas(List<ISourceCfg> sourceConfigs) {
        List<SourceData<?, T>> sourceDatas = sourceConfigs.stream().limit(2).map(SourceData<Object, T>::new).collect(Collectors.toList());

        buildSources(sourceDatas);

        buildMappers(sourceDatas);

        Class<T> commonClass = checkCommonGeneratedClass(sourceDatas);

        mapSources(sourceDatas, commonClass);

        return sourceDatas;
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

    private <T> Stream<Difference<?>> runStreamEquality(StreamEqualizer<T> streamEquality, List<SourceData<?, T>> sourceDatas) {
        Stream<Difference<?>> comparisonResult = null;
        SourceData<?, T> sourceData0 = sourceDatas.get(0);
        SourceData<?, T> sourceData1 = sourceDatas.get(1);
        //try (
                Source<T> sourceSrc = sourceData0.mappedSource;
                Source<T> targetSrc = sourceData1.mappedSource;
        //        ){
            Stream<T> sourceStream = sourceSrc.stream();
            Stream<T> targetStream = targetSrc.stream();
            comparisonResult = streamEquality.underlyingDiffs(sourceStream, targetStream);
            return comparisonResult;
//        } catch (Source.ClosingException e) {
//            logger.warn("Unable to close sources.", e);
//            // Misleading warning in Intellij. See https://youtrack.jetbrains.com/issue/IDEA-181860
//            return comparisonResult;
//        }
    }

    private <V> Equalizer<V> getEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd, equalityConfigs);
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
    }

    private <V> Comparator<V> getComparableEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Comparator<V>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof Comparator)
                .map(eq -> (Comparator<V>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        throw new RuntimeException("Can't find any comparable equality for field " + fd);
    }

    private <V> Hasher<V> getCategorizerEquality(FieldDescription<V> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Hasher<V>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof Hasher)
                .map(eq -> (Hasher<V>) eq).findFirst();
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
