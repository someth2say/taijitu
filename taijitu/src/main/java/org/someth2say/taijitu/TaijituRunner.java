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
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.database.ConnectionManager;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.registry.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.registry.ComparisonStrategyRegistry;
import org.someth2say.taijitu.tuple.ResultSetTupleBuilder;

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

        QueryConfig sourceQueryConfig = config.getSourceQueryConfig();
        FieldMatcher sourceMatcher = MatcherRegistry.getIdentityMatcher();
        ResultSetTupleBuilder sourceTupleBuilder = new ResultSetTupleBuilder(sourceMatcher, context, sourceQueryConfig);
        ResultSetIterator sourceIterator = getAndRegisterResultSetIterator(context, sourceMatcher, sourceQueryConfig, sourceTupleBuilder);
        if (sourceIterator != null) {
            final FieldMatcher targetMatcher = MatcherRegistry.getMatcher(config.getMatchingStrategyName());
            if (targetMatcher == null) {
                logger.error("Unable to find matching strategy '" + config.getMatchingStrategyName() + "'");
            } else {

                QueryConfig targetQueryConfig = config.getTargetQueryConfig();
                ResultSetTupleBuilder targetTupleBuilder = new ResultSetTupleBuilder(targetMatcher, context, targetQueryConfig);
                ResultSetIterator targetIterator = getAndRegisterResultSetIterator(context, targetMatcher, targetQueryConfig, targetTupleBuilder);
                if (targetIterator != null) {
                    return strategy.runComparison(sourceIterator, targetIterator, context, config);
                }
            }
        }
        return null;
    }


    private ResultSetIterator getAndRegisterResultSetIterator(ComparisonContext context, FieldMatcher fieldMatcher, QueryConfig queryConfig, ResultSetTupleBuilder tupleBuilder) {
        ResultSetIterator rsIterator = getQueryIterator(queryConfig, tupleBuilder);
        if (rsIterator == null) {
            return null;
        }
        if (!context.registerFields(rsIterator.getFields(), queryConfig, fieldMatcher)) {
            return null;
        }
        return rsIterator;
    }

    //TODO: Move to TupleIterator (something that provide both field names and tuple stream)
    private ResultSetIterator getQueryIterator(final QueryConfig queryConfig,
                                               ResultSetTupleBuilder tupleBuilder) {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection(queryConfig.getDatabaseRef());
        } catch (SQLException e) {
            logger.error("Unable to connect to " + queryConfig.getDatabaseRef(), e);
            return null;
        }

        final int fetchSize = queryConfig.getFetchSize();
        final Object[] queryParameters = queryConfig.getQueryParameters();
        final String statement = queryConfig.getStatement();
        return new ResultSetIterator(connection, statement, tupleBuilder, fetchSize, queryParameters);
    }


}
