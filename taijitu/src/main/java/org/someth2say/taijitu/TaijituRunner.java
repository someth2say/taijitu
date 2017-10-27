package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.SourceConfig;
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

    private final ComparisonConfig config;

    public TaijituRunner(final ComparisonConfig config) throws TaijituException {
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
        Map<PluginConfig, TaijituPlugin> plugins = PluginRegistry.getPlugins(config.getComparisonPluginConfigs());

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
                                          Map<PluginConfig, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<PluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().postComparison(comparison, entry.getKey());
        }
    }

    private void runPluginsPreComparison(final ComparisonContext comparison,
                                         Map<PluginConfig, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<PluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().preComparison(comparison, entry.getKey());
        }
    }

    private ComparisonResult runComparison(ComparisonContext context, ComparisonConfig comparisonConfig) {
        // Show comparison description
        final String strategyName = comparisonConfig.getStrategyConfig().getName();
        logger.info("COMPARISON: " + comparisonConfig.getName() + "(strategy " + strategyName + ")");
        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(strategyName);
        if (strategy != null) {
            return runComparisonWithStrategy(context, strategy, comparisonConfig);
        } else {
            logger.error("Unable to get comparison strategy " + strategyName);
        }
        return null;
    }

    private ComparisonResult runComparisonWithStrategy(ComparisonContext context, ComparisonStrategy strategy, ComparisonConfig comparisonConfig) {
        //Shall we use the same matcher for canonical source? No, we should use identity matcher....
        final FieldMatcher matcher = MatcherRegistry.getMatcher(config.getMatchingStrategyName());
        if (matcher != null) {
            List<SourceConfig> sourceConfigs = comparisonConfig.getSourceConfigs();
            List<Source> sources = sourceConfigs.stream().map(sourceConfig -> buildSource(sourceConfig, context, config)).collect(Collectors.toList());
            if (!sources.contains(null)) {
                return runComparisonForSources(context, strategy, comparisonConfig, matcher, sources);
            } else {
                //Some source failed creation
                return null;
            }
        } else {
            logger.error("Unable to find matching strategy '" + config.getMatchingStrategyName() + "'");
            return null;
        }
    }

    private ComparisonResult runComparisonForSources(ComparisonContext context, ComparisonStrategy strategy, ComparisonConfig comparisonConfig, FieldMatcher matcher, List<Source> sources) {
        if (sources.size() >= 2) {
            boolean anyRegisterFailure = sources.stream().map(source -> registerSourceFieldsToContext(matcher, context, source)).anyMatch(Boolean.FALSE::equals);
            if (!anyRegisterFailure) {
                //TODO: Generify
                return strategy.runComparison(sources.get(0), sources.get(1), context, config);
            } else {
                return null;
            }
        } else {
            logger.error("There should be at least 2 sources available in comparison " + comparisonConfig.getName() + " but " + sources.size() + " available");
            return null;
        }
    }

    private Source buildSource(final SourceConfig sourceConfig, ComparisonContext context, ComparisonConfig comparisonConfig) {

        //TODO: Decide the type for the source based on the config! Maybe delegating to a Source type...
        Class<? extends Source> sourceType = SourceTypeRegistry.getSourceType(sourceConfig.getType());

        try {
            Expression constructorExpression = new Expression(sourceType, "new", new Object[]{sourceConfig, comparisonConfig, context});
            Source source = (Source) constructorExpression.getValue();
            //if (registerSourceFieldsToContext(matcher, context, source)) {
            return source;
            //}
        } catch (Exception e) {
            logger.error("Unable to create source for " + sourceConfig.getName(), e);
        }
        return null;
    }

    private boolean registerSourceFieldsToContext(FieldMatcher matcher, ComparisonContext context, Source source) {
        return context.registerFields(source.getFieldDescriptions(), source.getConfig(), matcher);
    }

}
