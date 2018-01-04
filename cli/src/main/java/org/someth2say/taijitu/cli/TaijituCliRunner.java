package org.someth2say.taijitu.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.cli.registry.MapperRegistry;
import org.someth2say.taijitu.cli.registry.SourceRegistry;
import org.someth2say.taijitu.cli.registry.ValueEqualityRegistry;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.mapper.SourceMapper;
import org.someth2say.taijitu.cli.util.ClassScanUtils;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeComparator;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeEqualizer;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeHasher;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.simple.SimpleStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.sorted.ComparableStreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;

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
        return runComparison(comparisonCfg);
    }

    private class SourceData<GENERATED_TYPE, MAPPED_TYPE> {
        final ISourceCfg sourceCfg;
        Source<GENERATED_TYPE> source;
        Source<MAPPED_TYPE> mappedSource;
        SourceMapper<GENERATED_TYPE, MAPPED_TYPE> mapper;

        private SourceData(ISourceCfg sourceCfg) {
            this.sourceCfg = sourceCfg;
        }
    }

    public <GENERATED_TYPE, MAPPED_TYPE> Stream<Difference<?>> runComparison(IComparisonCfg iComparisonCfg) {

        List<ISourceCfg> sourceConfigs = iComparisonCfg.getSourceConfigs();
        if (sourceConfigs.size() < 2) {
            throw new RuntimeException(
                    "There should be at least 2 sources configured, but only " + sourceConfigs.size() + " found");
        }
        if (sourceConfigs.size() > 2) {
            logger.warn("More than 2 sources found ({}}). Only first two will be considered!", sourceConfigs.size());
        }

        // 0. Build and map sources
        List<SourceData<GENERATED_TYPE, MAPPED_TYPE>> sourceDatas = buildSourceDatas(sourceConfigs);

        // 2. Calculate common fields (given all sources)
        List<Source<MAPPED_TYPE>> mappedSources = sourceDatas.stream().map(sd -> sd.mappedSource).collect(Collectors.toList());
        final StreamEqualizer<MAPPED_TYPE> streamEquality = getStreamEqualizer(iComparisonCfg, mappedSources);

        logger.info("Comparison {} ready to run.", iComparisonCfg.getName());
        return runStreamEquality(streamEquality, sourceDatas);
    }

    private <MAPPED_TYPE> StreamEqualizer<MAPPED_TYPE> getStreamEqualizer(IComparisonCfg iComparisonCfg,
                                                                          List<Source<MAPPED_TYPE>> mappedSources) {
        Map<String, FieldDescription<?>> commonFDs = getCommonFields(mappedSources);
        List<FieldDescription<?>> keyFDs = iComparisonCfg.getKeyFields().stream().map(commonFDs::get).collect(Collectors.toList());
        List<FieldDescription<?>> sortFDs = iComparisonCfg.getSortFields().stream().map(commonFDs::get).collect(Collectors.toList());
        List<String> compareFDS = iComparisonCfg.getCompareFields();
        List<IEqualityCfg> equalityConfigs = iComparisonCfg.getEqualityConfigs();

        // 3.1.- If Key fields present, build hasher
        CompositeHasher<MAPPED_TYPE> hasher = getHasher(keyFDs, mappedSources, equalityConfigs);

        // 3.2.- If Sort fields present, build comparator
        CompositeComparator<MAPPED_TYPE> sorter = getComparator(sortFDs, mappedSources, equalityConfigs);

        // 3.3.- If Compare fields present, use for equality. Else, use all fields not in hasher/comparator
        CompositeEqualizer<MAPPED_TYPE> equality = getEquality(commonFDs, keyFDs, sortFDs, compareFDS, mappedSources, equalityConfigs);

        // 4. Run SteamEquality given Hasher and MappedStreams
        return buildEqualizer(hasher, sorter, equality);
    }

    private <MAPPED_TYPE> StreamEqualizer<MAPPED_TYPE> buildEqualizer(CompositeHasher<MAPPED_TYPE> hasher, CompositeComparator<MAPPED_TYPE> sorter, CompositeEqualizer<MAPPED_TYPE> equality) {
        final StreamEqualizer<MAPPED_TYPE> streamEquality;
        if (equality == null) {
            throw new RuntimeException("Unable to define comparison fields!");
        } else {
            if (sorter != null && hasher != null) {
                throw new RuntimeException("Hybrid equality not supported yet. Please use only Keys or Sort fields");
            } else {
                if (hasher != null) {
                    streamEquality = new HashingStreamEqualizer<>(equality, hasher);
                } else if (sorter != null) {
                    streamEquality = new ComparableStreamEqualizer<>(equality, sorter);
                } else {
                    streamEquality = new SimpleStreamEqualizer<>(equality);
                }
            }
        }
        return streamEquality;
    }

    private <MAPPED_TYPE> CompositeEqualizer<MAPPED_TYPE> getEquality(Map<String, FieldDescription<?>> commonFDs,
                                                                      List<FieldDescription<?>> keyFDs,
                                                                      List<FieldDescription<?>> sortFDs,
                                                                      List<String> compareFDS,
                                                                      List<Source<MAPPED_TYPE>> mappedSources,
                                                                      List<IEqualityCfg> equalityConfigs) {
        CompositeEqualizer<MAPPED_TYPE> equality = null;
        List<FieldDescription<?>> actualCompareFDs =
                (!compareFDS.isEmpty()
                        ? commonFDs.values().stream().filter(commonFD -> compareFDS.contains(commonFD.getName())) //TODO: Check that all compare fields actually are provided
                        : commonFDs.values().stream().filter(commonFD -> !keyFDs.contains(commonFD) && !sortFDs.contains(commonFD))).collect(Collectors.toList());
        if (!actualCompareFDs.isEmpty()) {
            CompositeEqualizer.Builder<MAPPED_TYPE> equalityBuilder = new CompositeEqualizer.Builder<>();
            actualCompareFDs.forEach(fd -> addEqualityComponent(equalityBuilder, fd, mappedSources, equalityConfigs));
            equality = equalityBuilder.build();
        }
        return equality;
    }

    private <GENERATED_TYPE, MAPPED_TYPE> CompositeComparator<MAPPED_TYPE> getComparator(List<FieldDescription<?>> sortFDs,
                                                                                         List<Source<MAPPED_TYPE>> mappedSources,
                                                                                         List<IEqualityCfg> equalityConfigs) {
        CompositeComparator<MAPPED_TYPE> sorter = null;
        if (!sortFDs.isEmpty()) {
            CompositeComparator.Builder<MAPPED_TYPE> comparerBuilder = new CompositeComparator.Builder<>();
            sortFDs.forEach(fd -> addComparerComponent(comparerBuilder, fd, mappedSources, equalityConfigs));
            sorter = comparerBuilder.build();
        }
        return sorter;
    }

    private <MAPPED_TYPE> CompositeHasher<MAPPED_TYPE> getHasher(List<FieldDescription<?>> keyFDs,
                                                                 List<Source<MAPPED_TYPE>> mappedSources,
                                                                 List<IEqualityCfg> equalityConfigs) {
        CompositeHasher<MAPPED_TYPE> hasher = null;
        if (!keyFDs.isEmpty()) {
            CompositeHasher.Builder<MAPPED_TYPE> categorizerBuilder = new CompositeHasher.Builder<>();
            keyFDs.forEach(fd -> addCategorizerComponent(categorizerBuilder, fd, mappedSources, equalityConfigs));
            hasher = categorizerBuilder.build();
        }
        return hasher;
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addEqualityComponent(CompositeEqualizer.Builder<MAPPED_TYPE> equalityBuilder,
                                                                FieldDescription<VALUE_TYPE> fd,
                                                                List<Source<MAPPED_TYPE>> mappedSources,
                                                                List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.get(0);
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Equalizer<VALUE_TYPE> vEqualizer = getEquality(fd, equalityConfigs);
        equalityBuilder.addComponent(extractor, vEqualizer);
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addCategorizerComponent(CompositeHasher.Builder<MAPPED_TYPE> categorizerBuilder,
                                                                   FieldDescription<VALUE_TYPE> fd,
                                                                   List<Source<MAPPED_TYPE>> mappedSources,
                                                                   List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.get(0); // Here we are assuming the all same extractor can be used for all sources (a.k.a. non-hybrid equality)
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Hasher<VALUE_TYPE> vEquality = getCategorizerEquality(fd, equalityConfigs);
        categorizerBuilder.addComponent(extractor, vEquality);
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addComparerComponent(CompositeComparator.Builder<MAPPED_TYPE> comparerBuilder,
                                                                FieldDescription<VALUE_TYPE> fd,
                                                                List<Source<MAPPED_TYPE>> mappedSources,
                                                                List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.get(0); // Here we are assuming the all same extractor can be used for all sources (a.k.a. non-hybrid equality)
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Comparator<VALUE_TYPE> vEquality = getComparableEquality(fd, equalityConfigs);
        comparerBuilder.addComponent(extractor, vEquality);
    }

    private <GENERATED_TYPE, MAPPED_TYPE> List<SourceData<GENERATED_TYPE, MAPPED_TYPE>> buildSourceDatas(List<ISourceCfg> sourceConfigs) {
        return sourceConfigs.stream().limit(2)
                .map(sourceConfig -> {
                    SourceData<GENERATED_TYPE, MAPPED_TYPE> sourceData = new SourceData<>(sourceConfig);
                    sourceData.source = buildSource(sourceConfig);
                    sourceData.mapper = buildMapper(sourceConfig);
                    sourceData.mappedSource = mapSource(sourceData.source, sourceData.mapper);
                    return sourceData;
                }).collect(Collectors.toList());
    }

    private <GENERATED_TYPE, MAPPED_TYPE> Source<MAPPED_TYPE> mapSource(Source<GENERATED_TYPE> source, SourceMapper<GENERATED_TYPE, MAPPED_TYPE> mapper) {
        if (mapper == null) {
            // TODO: What should we do with this unchecked cast?
            return (Source<MAPPED_TYPE>) source;
        } else {
            logger.debug("Applying mapper {} to source {} to produce composite type {}", ClassScanUtils.getClassName(mapper.getClass()),
                    source.getName(), mapper.getTypeParameter().getSimpleName());
            return mapper.apply(source);
        }
    }

    private <GENERATED_TYPE, MAPPED_TYPE> SourceMapper<GENERATED_TYPE, MAPPED_TYPE> buildMapper(ISourceCfg sourceCfg) {
        String mapperName = sourceCfg.getMapper();
        if (mapperName != null) {
            SourceMapper<GENERATED_TYPE, MAPPED_TYPE> mapper = MapperRegistry.getInstance(mapperName);
            if (mapper == null) {
                throw new RuntimeException(
                        "Can't find mapper instance: " + mapperName);
            }
            return mapper;
        } else {
            return null;
        }
    }

    private <GENERATED_TYPE> Source<GENERATED_TYPE> buildSource(ISourceCfg sourceCfg) {
        Source<GENERATED_TYPE> source = SourceRegistry.getInstance(sourceCfg.getType(), sourceCfg);
        if (source == null) {
            throw new RuntimeException("Can't find source type " + sourceCfg.getType());
        }
        return source;
    }

    private <GENERATED_TYPE, MAPPED_TYPE> Stream<Difference<?>> runStreamEquality(StreamEqualizer<MAPPED_TYPE> streamEquality, List<SourceData<GENERATED_TYPE, MAPPED_TYPE>> sourceDatas) {
        SourceData<?, MAPPED_TYPE> sourceData0 = sourceDatas.get(0);
        SourceData<?, MAPPED_TYPE> sourceData1 = sourceDatas.get(1);
        Source<MAPPED_TYPE> sourceSrc = sourceData0.mappedSource;
        Source<MAPPED_TYPE> targetSrc = sourceData1.mappedSource;
        Stream<MAPPED_TYPE> sourceStream = sourceSrc.stream();
        Stream<MAPPED_TYPE> targetStream = targetSrc.stream();
        return streamEquality.underlyingDiffs(sourceStream, targetStream);
    }

    private <VALUE_TYPE> Equalizer<VALUE_TYPE> getEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd, equalityConfigs);
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
    }

    private <VALUE_TYPE> Comparator<VALUE_TYPE> getComparableEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Comparator<VALUE_TYPE>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof Comparator)
                .map(eq -> (Comparator<VALUE_TYPE>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        throw new RuntimeException("Can't find any comparable equality for field " + fd);
    }

    private <VALUE_TYPE> Hasher<VALUE_TYPE> getCategorizerEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Hasher<VALUE_TYPE>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> ValueEqualityRegistry.getInstance(cfg.getName(), cfg.getEqualityParameters()))
                .filter(eq -> eq instanceof Hasher)
                .map(eq -> (Hasher<VALUE_TYPE>) eq).findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        throw new RuntimeException("Can't find any categorizer equality for field " + fd);
    }

    private <VALUE_TYPE> List<IEqualityCfg> getEqualityConfigsFor(FieldDescription<VALUE_TYPE> fd, final List<IEqualityCfg> equalityCfgs) {
        final String fieldClass = fd.getClazz().getName();
        final String fieldName = fd.getName();
        List<IEqualityCfg> perfectMatchEqualities = equalityCfgs.stream()
                .filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq))
                .collect(Collectors.toList());
        if (!perfectMatchEqualities.isEmpty())
            return perfectMatchEqualities;
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

    private <VALUE_TYPE> IEqualityCfg getEqualityConfigFor(FieldDescription<VALUE_TYPE> fieldDescription, final List<IEqualityCfg> equalityConfigIfaces) {
        final String fieldClass = fieldDescription.getClazz().getName();
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

    private <MAPPED_TYPE> Map<String, FieldDescription<?>> getCommonFields(List<Source<MAPPED_TYPE>> sources) {
        return getCanonicalFDs(sources).stream().collect(Collectors.toMap(FieldDescription::getName, Function.identity()));
    }

    private <MAPPED_TYPE> List<FieldDescription<?>> getCanonicalFDs(List<Source<MAPPED_TYPE>> sources) {
        List<FieldDescription<?>> canonicalFDs = sources.get(0).getProvidedFields();
        // Retain only canonicalFields that are also provided by other streams.
        for (Source<MAPPED_TYPE> source : sources.stream().skip(1).collect(Collectors.toList())) {
            List<FieldDescription<?>> sourceFDs = source.getProvidedFields();
            canonicalFDs.retainAll(sourceFDs);
        }
        return canonicalFDs;
    }

}
