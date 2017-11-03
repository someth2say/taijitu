package org.someth2say.taijitu;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.registry.ComparisonStrategyRegistry;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.registry.PluginRegistry;
import org.someth2say.taijitu.registry.SourceTypeRegistry;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.strategy.ComparisonStrategy;

import java.beans.Expression;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        ComparisonResult result = new SimpleComparisonResult(config);
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

    private ComparisonResult runComparisonWithStrategy(ComparisonContext context, ComparisonStrategy strategy, IComparisonCfg comparisonConfigIface) {
        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        String strategyName = config.getMatchingStrategyName();
        final FieldMatcher matcher = MatcherRegistry.getMatcher(strategyName);
        if (matcher != null) {
            List<ISourceCfg> sourceConfigIfaces = comparisonConfigIface.getSourceConfigs();
            List<Source> sources = sourceConfigIfaces.stream().map(sourceConfig -> buildSource(sourceConfig, context, config)).collect(Collectors.toList());
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

    private ComparisonResult runComparisonForSources(ComparisonContext context, ComparisonStrategy strategy, IComparisonCfg iComparisonCfg, FieldMatcher matcher, List<Source> sources) {
        if (sources.size() >= 2) {
            boolean anyRegisterFailure = sources.stream().map(source -> registerSourceFieldsToContext(matcher, context, source)).anyMatch(Boolean.FALSE::equals);
            if (!anyRegisterFailure) {
                logger.info("Comparison " + iComparisonCfg.getName() + " ready to run. Fields: " + StringUtils.join(context.getCanonicalFields(), ",") + " Keys: " + StringUtils.join(context.getCanonicalKeys(), ","));

                Source source = sources.get(0);
                Source target = sources.get(1);
                ComparisonResult comparisonResult = null;
                try (source; target) {
                    comparisonResult = strategy.runComparison(source, target, context, config);
                    return comparisonResult;
                } catch (Exception e) {
                    logger.warn("Unable to close sources.", e);
                    return comparisonResult;
                }
            } else {
                return null;
            }
        } else {
            logger.error("There should be at least 2 sources available in comparison " + iComparisonCfg.getName() + " but " + sources.size() + " available");
            return null;
        }
    }

    private Source buildSource(final ISourceCfg sourceConfig, ComparisonContext context, IComparisonCfg comparisonConfig) {

        Class<? extends Source> sourceType = SourceTypeRegistry.getSourceType(sourceConfig.getType());
        try {
            Expression constructorExpression = new Expression(sourceType, "new", new Object[]{sourceConfig, comparisonConfig, context});
            return (Source) constructorExpression.getValue();
        } catch (Exception e) {
            logger.error("Unable to create source for " + sourceConfig.getName(), e);
        }
        return null;
    }

    private boolean registerSourceFieldsToContext(FieldMatcher matcher, ComparisonContext context, Source source) {
        return context.registerFields(source.getFieldDescriptions(), source.getConfig(), matcher);
    }

}
