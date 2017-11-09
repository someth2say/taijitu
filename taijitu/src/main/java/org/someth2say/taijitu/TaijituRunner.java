package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;
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
import org.someth2say.taijitu.source.query.ResultSetTupleBuilder;
import org.someth2say.taijitu.tuple.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
        ComparisonContext context = new ComparisonContext(config);
        Map<IPluginCfg, TaijituPlugin> plugins = PluginRegistry.getPlugins(config.getComparisonPluginConfigs());

        try {

            runPluginsPreComparison(context, plugins);

            result = runComparison(context, config);

            runPluginsPostComparison(context, plugins);

        } catch (final TaijituException e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    private void runPluginsPostComparison(final ComparisonContext comparison,
                                          Map<IPluginCfg, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<IPluginCfg, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().postComparison(comparison, entry.getKey());
        }
    }

    private void runPluginsPreComparison(final ComparisonContext comparison,
                                         Map<IPluginCfg, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<IPluginCfg, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().preComparison(comparison, entry.getKey());
        }
    }

    private <T> ComparisonResult<T> runComparison(ComparisonContext context, IComparisonCfg iComparisonCfg) {

        IStrategyCfg strategyConfig = iComparisonCfg.getStrategyConfig();
        final String strategyName = strategyConfig.getName();
        logger.info("COMPARISON: " + iComparisonCfg.getName() + "(stream " + strategyName + ")");

        final FieldMatcher matcher = buildFieldMatcher(iComparisonCfg);
        if (matcher == null) return null;

        // TODO: Again restricting to tuple...
        ResultSetTupleBuilder builder = new ResultSetTupleBuilder(matcher, null);

        List<Source<T>> sources = iComparisonCfg.getSourceConfigs().stream().map(sourceConfig -> this.<T>buildSource(sourceConfig, context, iComparisonCfg, builder)).collect(Collectors.toList());
        if (sources.contains(null)) {
            logger.error("There was a problem building sources. Aborting.");
            return null;
        }
        if (sources.size() < 2) {
            logger.error("Not enough sources available. There should be at least 2 (third and following will be ignored)");
            return null;
        }

        List<FieldDescription> canonicalFields = getCanonicalFields(sources, matcher);

        // Update sources, so they know the actual fields we want to be returned
        sources.forEach(s -> s.setCanonicalFields(canonicalFields));

        List<FieldDescription> keyFields = getKeyFields(iComparisonCfg, canonicalFields);
        if (keyFields == null) {
            //TODO: Fine-grain log this.
            logger.error("Some keys are not provided by sources!");
            return null;
        }

        //TODO: Find a better way to validate stream equality requirements...
        // Sorted->ComparableValueEquality, Mapping->ValueEquality
        final StructureEquality<T> categorizer;
        Map<FieldDescription, ? extends ValueEquality<?>> equalities = getEqualities(keyFields, iComparisonCfg);
        switch (strategyName) {
            case ComparableStreamEquality.NAME:
                if (equalities.values().stream().allMatch(ve -> ve instanceof ComparableValueEquality<?>)) {
                    Map<FieldDescription, ComparableValueEquality<?>> comparableEqualities = (Map<FieldDescription, ComparableValueEquality<?>>) equalities;
                    categorizer = (StructureEquality<T>) new ComparableTupleEquality(comparableEqualities, canonicalFields);
                } else {
                    return null;
                }
                break;
            case MappingStreamEquality.NAME:
            default:
                categorizer = (StructureEquality<T>) new TupleEquality(equalities, canonicalFields);
        }

        List<FieldDescription> nonKeyFields = getNonKeyFields(canonicalFields, keyFields);
        Map<FieldDescription, ValueEquality<?>> nonKeyFieldsEqualities = getEqualities(nonKeyFields, iComparisonCfg);

        //TODO: Decide the type of structure equality based on the type of structure (tuple, here).
        final StructureEquality<T> equality = (StructureEquality<T>) new TupleEquality(nonKeyFieldsEqualities, canonicalFields);

        final StreamEquality<T> streamEquality = StreamEqualityRegistry.getInstance(strategyName, equality, categorizer);


        if (streamEquality == null) {
            return null;
        }

        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        return runComparisonForSources(streamEquality, iComparisonCfg, sources);
    }


    private <T> ComparisonResult<T> runComparisonForSources(StreamEquality<T> streamEquality, IComparisonCfg iComparisonCfg, List<Source<T>> sources) {
        if (sources.size() >= 2) {

            logger.info("Comparison " + iComparisonCfg.getName() + " ready to run.");

            ComparisonResult<T> comparisonResult = null;
            try (Source<T> source = sources.get(0); Source<T> target = sources.get(1)) {
                comparisonResult = streamEquality.runExternalComparison(source, target);
                return comparisonResult;
            } catch (Source.ClosingException e) {
                logger.warn("Unable to close sources.", e);
                // Misleading warning. See https://youtrack.jetbrains.com/issue/IDEA-181860
                return comparisonResult;
            }

        } else {
            logger.error("There should be at least 2 sources available in comparison " + iComparisonCfg.getName() + " but " + sources.size() + " available");
            return null;
        }
    }

    private Map<FieldDescription, ValueEquality<?>> getEqualities(List<FieldDescription> fieldDescriptions, IComparisonCfg iComparisonCfg) {
        return fieldDescriptions.stream().collect(Collectors.toMap(fd -> fd, fd -> getEquality(fd, iComparisonCfg)));
    }
//
//    private Map<FieldDescription, ComparableValueEquality<?>> getCategorizingEqualites(List<FieldDescription> fieldDescriptions, IComparisonCfg iComparisonCfg) {
//        return fieldDescriptions.stream().collect(Collectors.toMap(fd -> fd, fd -> getCategorizingEquality(fd, iComparisonCfg)));
//    }
//
//
//    private ComparableValueEquality<?> getCategorizingEquality(FieldDescription fieldDescription, IComparisonCfg iComparisonCfg) {
//        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fieldDescription.getClazz(), fieldDescription.getName(), iComparisonCfg.getEqualityConfigs());
//        AbstractValueEquality<?> valueEquality = ValueEqualityRegistry.getInstance(iEqualityCfg.getName(), iEqualityCfg.getEqualityParameters());
//        if (valueEquality instanceof ComparableValueEquality) {
//            return (ComparableValueEquality<?>) valueEquality;
//        } else {
//            logger.error("Equality for key " + fieldDescription.getName() + " is " + valueEquality.getName() + ", but key fields require a comparable equality.");
//            return null;
//        }
//    }


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

    private <T> List<FieldDescription> getCanonicalFields(List<Source<T>> sources, FieldMatcher matcher) {
        List<FieldDescription> canonicalFDs = sources.iterator().next().getProvidedFields();
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

    private <T> Source<T> buildSource(final ISourceCfg sourceConfig, ComparisonContext context, IComparisonCfg comparisonConfig, TupleBuilder<?> builder) {
        //TODO: Somehow make <T> explicit (extracting the class from the sourceConfig)
        return SourceRegistry.getInstance(sourceConfig.getType(), sourceConfig, comparisonConfig, context, builder);
    }


}
