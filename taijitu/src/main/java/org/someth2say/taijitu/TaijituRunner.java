package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;
import org.someth2say.taijitu.compare.equality.structure.ExtractorsAndEquality;
import org.someth2say.taijitu.compare.equality.structure.IStructureEquality;
import org.someth2say.taijitu.compare.equality.structure.StructureEquality;
import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.*;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.registry.*;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.source.mapper.SourceMapper;
import org.someth2say.taijitu.tuple.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jordi Sola
 */
public class TaijituRunner implements Callable<ComparisonResult> {

    private static final Logger logger = Logger.getLogger(TaijituRunner.class);

    private final IComparisonCfg config;

    public TaijituRunner(final IComparisonCfg config) throws TaijituException {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public ComparisonResult call() {
        ComparisonResult result = new SimpleComparisonResult();
        Map<IPluginCfg, TaijituPlugin> plugins = PluginRegistry.getPlugins(config.getComparisonPluginConfigs());

        try {

            runPluginsPreComparison(plugins);

            result = runComparison(config);

            runPluginsPostComparison(plugins);

        } catch (final TaijituException e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    private void runPluginsPostComparison(Map<IPluginCfg, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<IPluginCfg, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().postComparison(entry.getKey());
        }
    }

    private void runPluginsPreComparison(Map<IPluginCfg, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<IPluginCfg, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().preComparison(entry.getKey());
        }
    }


    private class SourceData<T> {
        final ISourceCfg sourceCfg;
        Source<T> source;
        List<ExtractorsAndEquality<T, ?>> eaes;
        List<Function<T, ?>> extractors;

        private SourceData(ISourceCfg sourceCfg) {
            this.sourceCfg = sourceCfg;
        }
    }

    private <T> ComparisonResult<T> runComparison(IComparisonCfg iComparisonCfg) {
        List<SourceData> sourceDatas = iComparisonCfg.getSourceConfigs().stream().map(SourceData::new).collect(Collectors.toList());

        //0. Build and map sources
        for (SourceData sourceData : sourceDatas) {
            Source source = SourceRegistry.getInstance(sourceData.sourceCfg.getType(), sourceData.sourceCfg);
            if (source == null) {
                throw new RuntimeException("Can't find source type " + sourceData.sourceCfg.getType());
            }
            String mapperName = sourceData.sourceCfg.getMapper();
            if (mapperName != null) {
                SourceMapper<Object, Object> instance = MapperRegistry.getInstance(mapperName);
                if (instance == null) {
                    throw new RuntimeException("Can't find mapper instance: " + mapperName + " for source " + source.getName());
                }
                sourceData.source = instance.apply(source);
            } else {
                sourceData.source = source;
            }
        }


        //1. Build matcher
        final FieldMatcher matcher = buildFieldMatcher(iComparisonCfg);
        if (matcher == null) return null;

        //2. Calculate canonical fields (gives all sources) (and matcher?)
        //TODO: Idea: Get rid of "canonicalFields", and force each source to provide the list of extractors...
        // In other words, replace the idea of "FieldDescription" by the idea of "ValueExtractor"
        List<FieldDescription<?>> canonicalFields = getCanonicalFields(sourceDatas.stream().map(sd -> sd.source).collect(Collectors.toList()), matcher);

        //3. Obtain ValueExtractors and Equalities from Canonical fields and MappedType/SourceMapper
        //Let's begin assuming all MappedType's are the same, so we can actually generate just one kind of ValueExtractors
        sourceDatas.forEach(sd -> sd.extractors = sd.source.getExtractors());

        //List maybeCanonicalFields = sourceDatas.get(0).source.getProvidedFields();
        //TODO: using canonicalFields here imply all streams produce exactly the same fields!
        List<ValueEquality> equalities = canonicalFields.stream().map(fd -> this.getEquality(fd, iComparisonCfg)).collect(Collectors.toList());




        sourceDatas.forEach((SourceData<T> sd) -> {
            sd.eaes = canonicalFields.stream().map(
                    (FieldDescription canonicalField) -> this.getExtractorAndEquality(iComparisonCfg, sd, canonicalField)
            ).collect(Collectors.toList());
        });

        //4. Build StructureEquality with ValueExtractors and ValueEqualities
        //TODO This is a big assumption, all sources map to the same type and use the same extractors!
        StructureEquality<?> structureEquality = new StructureEquality(sourceDatas.get(0).eaes);

        //5. Build MappedStreams given Sources and SourceMapper.


        //6. Run SteamEquality given IStructureEquality and MappedStreams


        //Mappers can't be done until all sources are done, 'cause they need canonical fields.
        List<Stream<T>> mappedStreams = sourceDatas.stream().map(sd1 -> sd1.source).collect(Collectors.toList()).stream().map((Source source) -> {
            ISourceCfg iSourceCfg = iComparisonCfg.getSourceConfigs().get(sourceDatas.stream().map(sd111 -> sd111.source).collect(Collectors.toList()).indexOf(source));
            Stream<T> stream = this.<Object, T>buildMappedStream(source, matcher, canonicalFields, iSourceCfg);

            List<FieldDescription> providedFields = source.getProvidedFields();

            //TODO: Grrrr.... too many unchecked casts...
            String mapperName = iSourceCfg.getMapper();
            if (mapperName == null) {
                return (Stream<T>) source.stream();
            }
            SourceMapper<R, T> sourceMapper = MapperRegistry.getInstance(mapperName);
            if (sourceMapper != null) {
                return source.stream().map(sourceMapper);
            }

            return null;


            return stream;
        }).collect(Collectors.toList());

        if (sourceDatas.stream().map(sd11 -> sd11.source).collect(Collectors.toList()).contains(null)) {
            logger.error("There was a problem building sources. Aborting.");
            return null;
        }
        if (sourceDatas.stream().map(sd1 -> sd1.source).collect(Collectors.toList()).size() < 2) {
            logger.error("Not enough sources available. There should be at least 2 (following will be ignored)");
            return null;
        }

        final StreamEquality<T> streamEquality = buildStreamEquality(iComparisonCfg, canonicalFields);

        if (streamEquality == null) return null;

        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        logger.info("Comparison " + iComparisonCfg.getName() + " ready to run.");
        return execStreamEquality(streamEquality, mappedStreams, sourceDatas.stream().map(sd -> sd.source).collect(Collectors.toList()));
    }

    private <T, V> ExtractorsAndEquality<T, V> getExtractorAndEquality(IComparisonCfg iComparisonCfg, SourceData<T> sd, FieldDescription<V> canonicalField) {
        //1. Obtain the extractor
        Function<?, V> extractor = sd.mapper != null ? sd.mapper.getExtractor(canonicalField) : sd.source.getExtractor(canonicalField);
        //2. Obtain the equality
        ValueEquality<V> equality = iComparisonCfg.getValueEquality(canonicalField);
        return new ExtractorsAndEquality<>(extractor, equality);
    }

//    private <T, V> ExtractorsAndEquality<T, V> getExtractorAndEquality(List<? extends ValueEquality<V>> valueEqualities, List<Function<T, V>> extractors, int i) {
//        return new ExtractorsAndEquality<>(extractors.get(i), valueEqualities.get(i));
//    }

    private <R, T> Stream<T> buildMappedStream(Source<R> source, FieldMatcher
            matcher, List<FieldDescription> canonicalFields, ISourceCfg iSourceCfg) {

    }

    private <T> StreamEquality<T> buildStreamEquality(IComparisonCfg
                                                              iComparisonCfg, List<FieldDescription> canonicalFields) {

        List<FieldDescription> keyFields = getKeyFields(iComparisonCfg, canonicalFields);
        if (keyFields == null) {
            //TODO: Fine-grain log this.
            logger.error("Some keys are not provided by sources!");
            return null;
        }

        String strategyName = iComparisonCfg.getStrategyConfig().getName();
        //TODO: Find a better way to validate stream equality requirements...
        // Sorted->ComparableValueEquality, Mapping->ValueEquality
        final IStructureEquality<T> categorizer;
        Map<FieldDescription, ? extends ValueEquality<?>> equalities = getEqualities(keyFields, iComparisonCfg);
        switch (strategyName) {
            case ComparableStreamEquality.NAME:
                if (equalities.values().stream().allMatch(ve -> ve instanceof ComparableValueEquality<?>)) {
                    Map<FieldDescription, ComparableValueEquality<?>> comparableEqualities = (Map<FieldDescription, ComparableValueEquality<?>>) equalities;
                    categorizer = (IStructureEquality<T>) new ComparableTupleEquality(comparableEqualities, canonicalFields);
                } else {
                    return null;
                }
                break;
            case MappingStreamEquality.NAME:
            default:
                categorizer = (IStructureEquality<T>) new TupleEquality(equalities, canonicalFields);
        }

        List<FieldDescription> nonKeyFields = getNonKeyFields(canonicalFields, keyFields);
        Map<FieldDescription, ValueEquality<?>> nonKeyFieldsEqualities = getEqualities(nonKeyFields, iComparisonCfg);

        //TODO: Decide the type of structure equality based on the type of structure (tuple, here).
        final IStructureEquality<T> equality = (IStructureEquality<T>) new TupleEquality(nonKeyFieldsEqualities, canonicalFields);

        final StreamEquality<T> streamEquality = StreamEqualityRegistry.getInstance(strategyName, equality, categorizer);


        if (streamEquality == null) {
            return null;
        }
        return streamEquality;
    }

    private <T> ComparisonResult<T> execStreamEquality
            (StreamEquality<T> streamEquality, List<Stream<T>> streams, List<Source> sources) {
        if (streams.size() >= 2) {
            ComparisonResult<T> comparisonResult = null;
            try (Stream<T> sourceStr = streams.get(0); Stream<T> targetStr = streams.get(1); Source sourceSrc = sources.get(0); Source targetSrc = sources.get(1)) {
                comparisonResult = streamEquality.runComparison(sourceStr, sourceSrc.getName(), targetStr, targetSrc.getName());
                return comparisonResult;
            } catch (Source.ClosingException e) {
                //TODO: We are actually closing the stream, not the source!!!!!
                logger.warn("Unable to close sources.", e);
                // Misleading warning. See https://youtrack.jetbrains.com/issue/IDEA-181860
                return comparisonResult;
            }

        } else {
            logger.error("There should be at least 2 sources, but " + streams.size() + " available");
            return null;
        }
    }

    private Map<FieldDescription, ValueEquality<?>> getEqualities
            (List<FieldDescription> fieldDescriptions, IComparisonCfg iComparisonCfg) {
        return fieldDescriptions.stream().collect(Collectors.toMap(fd -> fd, fd -> getEquality(fd, iComparisonCfg)));
    }

    private ValueEquality<?> getEquality(FieldDescription fieldDescription, IComparisonCfg iComparisonCfg) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fieldDescription.getClazz(), fieldDescription.getName(), iComparisonCfg.getEqualityConfigs());
        return ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
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
                logger.error("Class defined in equality config not found: " + configClassName);
                return false;
            }
        }
    }

    private List<FieldDescription> getNonKeyFields(List<FieldDescription> canonicalFields, List<FieldDescription> keyFields) {
        return canonicalFields.stream().filter(f -> !keyFields.contains(f)).collect(Collectors.toList());
    }

    private List<FieldDescription> getKeyFields(IComparisonCfg iComparisonCfg, List<FieldDescription> canonicalFields) {
        List<String> keyFields = iComparisonCfg.getKeyFields();
        List<FieldDescription> keyDescriptions = canonicalFields.stream().filter(cf -> keyFields.contains(cf.getName())).collect(Collectors.toList());
        //Should validate all keys are provided
        if (keyDescriptions.size() < keyFields.size()) {
            return null;
        }
        return keyDescriptions;
    }

    private List<FieldDescription<?>> getCanonicalFields(List<Source> sources, FieldMatcher matcher) {
        List<FieldDescription<?>> canonicalFDs = sources.iterator().next().getProvidedFields();
        // Retain only canonicalFields that are also provided by other streams.
        for (Source<?> source : sources.stream().skip(1).collect(Collectors.toList())) {
            List<FieldDescription> sourceFDs = source.getProvidedFields();
            sourceFDs.replaceAll(sourceFD -> matcher.getCanonicalField(sourceFD, canonicalFDs, sourceFDs));
            canonicalFDs.retainAll(sourceFDs);
        }
        return canonicalFDs;
    }

    private FieldMatcher buildFieldMatcher(IComparisonCfg iComparisonCfg) {
        //Build the whole equality structure (tree?)
        final FieldMatcher matcher = MatcherRegistry.getMatcher(iComparisonCfg.getMatchingStrategyName());
        if (matcher == null) {
            logger.error("Unable to find matching stream '" + iComparisonCfg.getMatchingStrategyName() + "'");
            return null;
        }
        return matcher;
    }


}
