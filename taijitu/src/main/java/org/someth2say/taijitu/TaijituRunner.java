package org.someth2say.taijitu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.source.ResultSetSource;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.database.ConnectionManager;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.registry.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.registry.ComparisonStrategyRegistry;

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

            result = runComparison(context);

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

    private ComparisonResult runComparison(ComparisonContext context) {
        // Show comparison description
        final String strategyName = config.getStrategyConfig().getName();
        logger.info("COMPARISON: " + config.getName() + "(strategy " + strategyName + ")");
        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(strategyName);
        if (strategy != null) {
            return runComparisonStrategy(context, strategy);
        } else {
            logger.error("Unable to get comparison strategy " + strategyName);
        }
        return null;
    }

    private ComparisonResult runComparisonStrategy(ComparisonContext context, ComparisonStrategy strategy) {

        //TODO: Maybe we should iterate on queryConfig's? Then we need to ensure config sanity
        Source sourceSource = buildSource(ConfigurationLabels.Comparison.SOURCE, MatcherRegistry.getIdentityMatcher(), context, config);
        if (sourceSource != null) {
            final FieldMatcher targetMatcher = MatcherRegistry.getMatcher(config.getMatchingStrategyName());
            Source targetSource = buildSource(ConfigurationLabels.Comparison.TARGET, targetMatcher, context, config);
            if (targetSource != null) {
                return strategy.runComparison(sourceSource, targetSource, context, config);
            }
        }
        return null;
    }

    private Source buildSource(final String sourceId, FieldMatcher matcher, ComparisonContext context, ComparisonConfig comparisonConfig) {
        if (matcher == null) {
            logger.error("Unable to find matching strategy '" + config.getMatchingStrategyName() + "'");
        }

        final QuerySourceConfig querySourceConfig = comparisonConfig.getSourceConfig(sourceId);

        //TODO: Decide the type for the source based on the config!
        Source source = getResultSetSource(querySourceConfig, comparisonConfig, context);


        if (source != null && registerSourceFieldsToContext(querySourceConfig, matcher, context, source)) {
            return source;
        }
        return null;
    }

    private boolean registerSourceFieldsToContext(QuerySourceConfig querySourceConfig, FieldMatcher matcher, ComparisonContext context, Source source) {
        return context.registerFields(source.getFieldDescriptions(), querySourceConfig, matcher);
    }


    private ResultSetSource getResultSetSource(final QuerySourceConfig querySourceConfig, final ComparisonConfig comparisonConfig, final ComparisonContext context) {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection(querySourceConfig.getDatabaseConfig());
        } catch (SQLException e) {
            logger.error("Unable to connect to database at " + querySourceConfig.getName(), e);
            return null;
        }

        return new ResultSetSource(connection, comparisonConfig, querySourceConfig.getName(), context);
    }


}
