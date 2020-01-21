package org.someth2say.taijitu.cli;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.cli.registry.MapperRegistry;
import org.someth2say.taijitu.cli.registry.SourceRegistry;
import org.someth2say.taijitu.cli.registry.EqualizerRegistry;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.mapper.SourceMapper;
import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.impl.composite.CompositeComparatorHasher;
import org.someth2say.taijitu.equality.impl.value.ComparableComparatorHasher;
import org.someth2say.taijitu.equality.impl.value.ObjectHasher;
import org.someth2say.taijitu.equality.impl.value.ObjectToStringComparatorHasher;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.stream.simple.SimpleStreamEqualizer;
import org.someth2say.taijitu.stream.sorted.ComparableStreamEqualizer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jordi Sola
 */
class TaijituCliRunner implements Callable<Stream<Difference<Object>>> {

    private static final Logger logger = LoggerFactory.getLogger(TaijituCliRunner.class);

    private final IComparisonCfg comparisonCfg;

    public TaijituCliRunner(final IComparisonCfg comparisonCfg) {
        this.comparisonCfg = comparisonCfg;
    }

    @Override
    public Stream<Difference<Object>> call() {
        return runComparison(comparisonCfg);
    }

    public <MAPPED_TYPE> Stream<Difference<MAPPED_TYPE>> runComparison(IComparisonCfg iComparisonCfg) {

        List<ISourceCfg> sourceConfigs = iComparisonCfg.getSourceConfigs();
        if (sourceConfigs.size() < 2) {
            throw new RuntimeException("There should be at least 2 sources configured, but only " + sourceConfigs.size() + " found");
        }
        if (sourceConfigs.size() > 2) {
            logger.warn("More than 2 sources found ({}}). Only first two will be considered!", sourceConfigs.size());
        }

        // 0. Build and map sources
        Duo<Source<MAPPED_TYPE>> mappedSources = buildMappedSources(sourceConfigs);

        // 2. Calculate common fields (given all sources)
        final StreamEqualizer<MAPPED_TYPE> streamEquality = getStreamEqualizer(iComparisonCfg, mappedSources);

        logger.info("ComparisonCfg {} ready to run: {} vs {}", iComparisonCfg.getName(), mappedSources.getLeft().getName(), mappedSources.getRight().getName());
        return runStreamEquality(streamEquality, mappedSources);
    }

    private <MAPPED_TYPE> StreamEqualizer<MAPPED_TYPE> getStreamEqualizer(IComparisonCfg iComparisonCfg,
                                                                          Duo<Source<MAPPED_TYPE>> mappedSources) {
        Map<String, FieldDescription<?>> commonFDs = getCommonFields(mappedSources);
        List<FieldDescription<?>> keyFDs = iComparisonCfg.getKeyFields().stream().map(commonFDs::get).filter(Objects::nonNull).collect(Collectors.toList());
        List<FieldDescription<?>> sortFDs = iComparisonCfg.getSortFields().stream().map(commonFDs::get).filter(Objects::nonNull).collect(Collectors.toList());

        List<FieldDescription<?>> compareFDS = (iComparisonCfg.getCompareFields().isEmpty()
                ? commonFDs.keySet()
                : iComparisonCfg.getCompareFields()).stream().map(commonFDs::get).filter(Objects::nonNull).collect(Collectors.toList());

        if (!iComparisonCfg.getCompareFields().isEmpty() && compareFDS.size() < iComparisonCfg.getCompareFields().size()) {
            logger.warn("Some of the comparison fields in configuration are not common to both sources, and will be ignored.");
            logger.debug("Common fields: ´{}", commonFDs.keySet());
        }

        List<IEqualityCfg> equalityConfigs = iComparisonCfg.getEqualityConfigs();

        CompositeComparatorHasher.Builder<MAPPED_TYPE> builder = new CompositeComparatorHasher.Builder<>();

        // 3.1.- If Key fields present, build hasher
        keyFDs.forEach(fd -> addHasherComponent(builder, fd, mappedSources, equalityConfigs));

        // 3.2.- If Sort fields present, build comparator
        sortFDs.forEach(fd->addComparerComponent(builder, fd, mappedSources, equalityConfigs));

        // 3.3.- If Compare fields present, use for equality. Else, use all fields not in hasher/comparator
        compareFDS.forEach(fd->addEqualityComponent(builder, fd, mappedSources, equalityConfigs));

        // 4. Run SteamEquality given Hasher and MappedStreams
        return buildEqualizer(builder.build());
    }

    private <MAPPED_TYPE> StreamEqualizer<MAPPED_TYPE> buildEqualizer(CompositeComparatorHasher<MAPPED_TYPE> equality) {
        final StreamEqualizer<MAPPED_TYPE> streamEquality;
        if (equality == null) {
            throw new RuntimeException("Unable to define comparison fields!");
        } else {
            if (!equality.getComparators().isEmpty() && !equality.getHashers().isEmpty()) {
                throw new RuntimeException("Hybrid stream equality not supported yet. Please use only Keys or Sort fields");
            } else {
                if (!equality.getHashers().isEmpty()) {
                    logger.debug("Hashing composites using " + equality);
                    streamEquality = new HashingStreamEqualizer<>(equality);
                } else if (!equality.getComparators().isEmpty()) {
                    logger.debug("Assuming composites ordered by using " + equality);
                    streamEquality = new ComparableStreamEqualizer<>(equality);
                } else {
                    logger.debug("No sort/hash fields provided, so applying positional comparison.");
                    streamEquality = new SimpleStreamEqualizer<>(equality);
                }
            }
        }
        logger.debug("Using stream equality strategy: {}", streamEquality.getClass().getSimpleName());
        return streamEquality;
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addEqualityComponent(CompositeComparatorHasher.Builder<MAPPED_TYPE> equalityBuilder,
                                                                FieldDescription<VALUE_TYPE> fd,
                                                                Duo<Source<MAPPED_TYPE>> mappedSources,
                                                                List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.getLeft(); // Here we are assuming the all same extractor can be used for all sources (a.k.a. non-hybrid equality)
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Equalizer<VALUE_TYPE> vEqualizer = getEquality(fd, equalityConfigs);
        equalityBuilder.addEqualizer(extractor, vEqualizer);
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addHasherComponent(CompositeComparatorHasher.Builder<MAPPED_TYPE> builder,
                                                              FieldDescription<VALUE_TYPE> fd,
                                                              Duo<Source<MAPPED_TYPE>> mappedSources,
                                                              List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.getLeft(); // Here we are assuming the all same extractor can be used for all sources (a.k.a. non-hybrid equality)
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Hasher<VALUE_TYPE> hasher = getHasherEquality(fd, equalityConfigs);
        builder.addHasher(extractor, hasher);
    }

    private <MAPPED_TYPE, VALUE_TYPE> void addComparerComponent(CompositeComparatorHasher.Builder<MAPPED_TYPE> comparerBuilder,
                                                                FieldDescription<VALUE_TYPE> fd,
                                                                Duo<Source<MAPPED_TYPE>> mappedSources,
                                                                List<IEqualityCfg> equalityConfigs) {
        Source<MAPPED_TYPE> mappedSource = mappedSources.getLeft(); // Here we are assuming the all same extractor can be used for all sources (a.k.a. non-hybrid equality)
        Function<MAPPED_TYPE, VALUE_TYPE> extractor = mappedSource.getExtractor(fd);
        Comparator<VALUE_TYPE> vEquality = getComparableEquality(fd, equalityConfigs);
        comparerBuilder.addComparator(extractor, vEquality);
    }

    private <MAPPED_TYPE> Duo<Source<MAPPED_TYPE>> buildMappedSources(List<ISourceCfg> sourceConfigs) {
        return new Duo<>(getMappedSource(sourceConfigs.get(0)), getMappedSource(sourceConfigs.get(1)));
    }

    private <GENERATED_TYPE, MAPPED_TYPE> Source<MAPPED_TYPE> getMappedSource(ISourceCfg sourceConfig) {
        Source<GENERATED_TYPE> source = buildSource(sourceConfig);
        SourceMapper<GENERATED_TYPE, MAPPED_TYPE> mapper = buildMapper(sourceConfig);
        return mapSource(source, mapper);
    }

    @SuppressWarnings("unchecked")
    private <GENERATED_TYPE, MAPPED_TYPE> Source<MAPPED_TYPE> mapSource(Source<GENERATED_TYPE> source, SourceMapper<GENERATED_TYPE, MAPPED_TYPE> mapper) {
        if (mapper == null) {
            return (Source<MAPPED_TYPE>) source;
        } else {
            Source<MAPPED_TYPE> mappedSource = mapper.apply(source);
            logger.debug("Applying mapper {} to source {}", mapper.getClass().getSimpleName(),source.getName());
            return mappedSource;
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

    private <MAPPED_TYPE> Stream<Difference<MAPPED_TYPE>> runStreamEquality(StreamEqualizer<MAPPED_TYPE> streamEquality,
                                                                  Duo<Source<MAPPED_TYPE>> mappedSources) {
        Source<MAPPED_TYPE> sourceSrc = mappedSources.getLeft();
        Source<MAPPED_TYPE> targetSrc = mappedSources.getRight();
        Stream<MAPPED_TYPE> sourceStream = sourceSrc.stream();
        Stream<MAPPED_TYPE> targetStream = targetSrc.stream();
        return streamEquality.explain(sourceStream, targetStream);
    }

    private <VALUE_TYPE> Equalizer<VALUE_TYPE> getEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fd, equalityConfigs);
        return iEqualityCfg!=null
                ? EqualizerRegistry.getInstance(iEqualityCfg.getName())
                : (Equalizer<VALUE_TYPE>) ObjectHasher.INSTANCE;
    }

    private <VALUE_TYPE> Comparator<VALUE_TYPE> getComparableEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Comparator<VALUE_TYPE>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> EqualizerRegistry.getInstance(cfg.getName()))
                .filter(eq -> eq instanceof Comparator)
                .map(eq -> (Comparator<VALUE_TYPE>) eq).findFirst();

        if (first.isPresent()) {
            return first.get();
        }

        if (fd.getClazz().isAssignableFrom(Comparable.class))
            return (Comparator<VALUE_TYPE>) ComparableComparatorHasher.INSTANCE;

        return (Comparator<VALUE_TYPE>) ObjectToStringComparatorHasher.INSTANCE;
    }

    private <VALUE_TYPE> Hasher<VALUE_TYPE> getHasherEquality(FieldDescription<VALUE_TYPE> fd, List<IEqualityCfg> equalityConfigs) {
        List<IEqualityCfg> compatibleEqualityConfigs = getEqualityConfigsFor(fd, equalityConfigs);

        Optional<Hasher<VALUE_TYPE>> first = compatibleEqualityConfigs.stream()
                .map(cfg -> EqualizerRegistry.getInstance(cfg.getName()))
                .filter(eq -> eq instanceof Hasher)
                .map(eq -> (Hasher<VALUE_TYPE>) eq)
                .findFirst();
        if (first.isPresent()) {
            return first.get();
        }

        return (Hasher<VALUE_TYPE>) ObjectHasher.INSTANCE;
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

    private <MAPPED_TYPE> Map<String, FieldDescription<?>> getCommonFields(Duo<Source<MAPPED_TYPE>> sources) {
        return getCanonicalFDs(sources).stream().collect(Collectors.toMap(FieldDescription::getName, Function.identity()));
    }

    private <MAPPED_TYPE> List<FieldDescription<?>> getCanonicalFDs(Duo<Source<MAPPED_TYPE>> sources) {
        List<FieldDescription<?>> canonicalFDs = sources.getLeft().getProvidedFields();
        canonicalFDs.retainAll(sources.getRight().getProvidedFields());
        return canonicalFDs;
    }

}

class Duo<T> extends Pair<T, T> {
    private final T left;
    private final T right;

    Duo(final T left, final T right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public T getLeft() {
        return left;
    }

    @Override
    public T getRight() {
        return right;
    }

    @Override
    public T setValue(T value) {
        throw new UnsupportedOperationException();
    }

}