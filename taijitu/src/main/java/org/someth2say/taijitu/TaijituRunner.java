package org.someth2say.taijitu;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.compare.equality.external.ExternalEquality;
import org.someth2say.taijitu.config.interfaces.*;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.registry.*;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleExternalEquality;

import java.beans.Expression;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
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

    private ComparisonResult runComparison(ComparisonContext context, IComparisonCfg iComparison) {
        // Show comparison description
        IStrategyCfg strategyConfig = iComparison.getStrategyConfig();
        final String strategyName = strategyConfig.getName();
        logger.info("COMPARISON: " + iComparison.getName() + "(strategy " + strategyName + ")");
        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(strategyName);
        if (strategy != null) {
            return runComparisonWithStrategy(context, strategy, iComparison);
        } else {
            logger.error("Unable to get comparison strategy " + strategyName);
        }
        return null;
    }

    private <T> ComparisonResult<T> runComparisonWithStrategy(ComparisonContext context, ComparisonStrategy strategy, IComparisonCfg comparisonConfigIface) {
        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        String strategyName = config.getMatchingStrategyName();
        final FieldMatcher matcher = MatcherRegistry.getMatcher(strategyName);
        if (matcher != null) {
            List<ISourceCfg> iSourceCfgs = comparisonConfigIface.getSourceConfigs();
            List<Source<T>> sources = iSourceCfgs.stream().map(sourceConfig -> this.<T>buildSource(sourceConfig, context, config)).collect(Collectors.toList());
            if (!sources.contains(null)) {
                return runComparisonForSources(context, strategy, comparisonConfigIface, matcher, sources);
            } else {
                //Some source failed creation
                return null;
            }
        } else {
            logger.error("Unable to find matching strategy '" + strategyName + "'");
            return null;
        }
    }

    private <T> ComparisonResult<T> runComparisonForSources(ComparisonContext context, ComparisonStrategy strategy, IComparisonCfg iComparisonCfg, FieldMatcher matcher, List<Source<T>> sources) {
        if (sources.size() >= 2) {

            //TODO: Here we are deciding that comparison fields are all common fields for all sources! Maybe we can shrink...
            List<FieldDescription> canonicalDescriptions = getCanonicalFields(matcher, sources);
            List<ValueEquality<?>> fieldEqualities = getEqualityStrategies(canonicalDescriptions, iComparisonCfg);
            List<Object> equalityParams ;
            int[] compareFieldsIdxs ;

            ExternalEquality<T> equality = (ExternalEquality<T>) new TupleExternalEquality(fieldEqualities, equalityParams, compareFieldsIdxs);

            //TODO: categorizer depends on the strategy: Mapping->ExternalEquality, Sorted->ExternalSortedEquality
            List<FieldDescription> keyDescriptions = getKeyFields(iComparisonCfg, canonicalDescriptions);
            if (keyDescriptions == null) return null;
            List<ValueEquality<?>> keyEqualities = getEqualityStrategies(keyDescriptions, iComparisonCfg);
            List<Object> keyEqualityParams ;
            int[] keyFieldsIdxs;
            ExternalEquality<T> categorizer = (ExternalEquality<T>) new TupleExternalEquality(keyEqualities, keyEqualityParams, keyFieldsIdxs);

            logger.info("Comparison " + iComparisonCfg.getName() + " ready to run. Fields: " + StringUtils.join(context.getCanonicalFields(), ",") + " Keys: " + StringUtils.join(context.getCanonicalKeys(), ","));

            ComparisonResult<T> comparisonResult = null;
            try (Source<T> source = sources.get(0); Source<T> target = sources.get(1)) {
                comparisonResult = strategy.runExternalComparison(source, target, categorizer, equality);
                return comparisonResult;
            } catch (Source.ClosingException e) {
                logger.warn("Unable to close sources.", e);
                return comparisonResult;
            }

        } else {
            logger.error("There should be at least 2 sources available in comparison " + iComparisonCfg.getName() + " but " + sources.size() + " available");
            return null;
        }
    }

    private List<ValueEquality<?>> getEqualityStrategies(List<FieldDescription> fieldDescriptions, IComparisonCfg iComparisonCfg) {
        return fieldDescriptions.stream().map(fd -> getEqualityStrategy(fd, iComparisonCfg)).collect(Collectors.toList())
    }

    private ValueEquality<?> getEqualityStrategy(FieldDescription fieldDescription, IComparisonCfg iComparisonCfg) {
        IEqualityCfg iEqualityCfg = getEqualityConfigFor(fieldDescription.getClazz(), fieldDescription.getName(), iComparisonCfg.getEqualityConfigs());
        return EqualityStrategyRegistry.getEqualityStrategy(iEqualityCfg.getName());
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

    private List<FieldDescription> getKeyFields(IComparisonCfg iComparisonCfg, List<FieldDescription> canonicalFields) {
        List<String> keyFields = iComparisonCfg.getKeyFields();
        List<FieldDescription> keyDescriptions = canonicalFields.stream().filter(cf -> keyFields.contains(cf.getName())).collect(Collectors.toList());
        //Should validate all keys are provided
        if (keyDescriptions.size() < keyFields.size()) {
            logger.error("Some keys are not provided by sources. Keys provided: " + StringUtils.join(keyDescriptions, ","));
            return null;
        }
        return keyDescriptions;
    }

    private <T> List<FieldDescription> getCanonicalFields(FieldMatcher matcher, List<Source<T>> sources) {
        List<FieldDescription> baseFDs = sources.iterator().next().getFieldDescriptions();
        return sources.stream().skip(1).map(Source::getFieldDescriptions)
                .map(fds -> fds.stream().map(fd -> matcher.getCanonicalField(fd, baseFDs, fds)))
                .reduce(baseFDs.stream(), (fdStream1, fdStream2) -> fdStream2.filter(fd -> fd != null && fdStream1.anyMatch(fd::equals))
                ).collect(Collectors.toList());
    }

    private <T> Source<T> buildSource(final ISourceCfg sourceConfig, ComparisonContext context, IComparisonCfg comparisonConfig) {

        Class<? extends Source> sourceType = SourceTypeRegistry.getSourceType(sourceConfig.getType());
        try {
            Expression constructorExpression = new Expression(sourceType, "new", new Object[]{sourceConfig, comparisonConfig, context});
            //TODO: Fix this unckecked cast
            return (Source<T>) constructorExpression.getValue();
        } catch (Exception e) {
            logger.error("Unable to create source for " + sourceConfig.getName(), e);
        }
        return null;
    }

}
