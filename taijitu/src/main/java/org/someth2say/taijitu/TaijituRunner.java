package org.someth2say.taijitu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.matcher.ColumnMatcher;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.ResultSetIterator;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.registry.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.query.QueryUtilsException;
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
    public ComparisonResult call() {
        ComparisonResult result = new ComparisonResult(config);
        ComparisonRuntime comparison = new ComparisonRuntime(config);
        Map<ComparisonPluginConfig, TaijituPlugin> plugins = PluginRegistry.getPlugins(config.getComparisonPluginConfigs());

        try {

            result.setStatus(ComparisonResult.ComparisonResultStatus.RUNNING);

            runPluginsPreComparison(comparison, plugins);

            runComparison(comparison);

            runPluginsPostComparison(comparison, plugins);

            result.setStatus(ComparisonResult.ComparisonResultStatus.SUCCESS);

        } catch (final TaijituException e) {
            logger.error(e.getMessage(), e);
            result.setStatus(ComparisonResult.ComparisonResultStatus.ERROR);
        }

        return result;
    }

    private void runPluginsPostComparison(final ComparisonRuntime comparison,
                                          Map<ComparisonPluginConfig, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<ComparisonPluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().postComparison(comparison, entry.getKey());
        }
    }

    private void runPluginsPreComparison(final ComparisonRuntime comparison,
                                         Map<ComparisonPluginConfig, TaijituPlugin> plugins) throws TaijituException {
        for (Entry<ComparisonPluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
            entry.getValue().preComparison(comparison, entry.getKey());
        }
    }

    private void runComparison(ComparisonRuntime comparison) {
        // Show comparison description
        logger.info("COMPARISON: " + config.getName() + "(strategy " + config.getStrategyConfig().getName() + ")");
        logger.debug("PARAMETERS: " + config.getAllParameters());
        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(config.getStrategyConfig().getName());
        if (strategy != null) {
            runComparisonStrategy(comparison, strategy);
        } else {
            logger.error("Unable to get comparison strategy " + config.getStrategyConfig().getName());
        }
    }

    private void runComparisonStrategy(ComparisonRuntime comparison, ComparisonStrategy strategy) {
        ResultSetIterator<ComparableTuple> sourceIterator = getAndRegisterResultSetIterator(comparison, MatcherRegistry.getIdentityMatcher(), config.getSourceQueryConfig());
        if (sourceIterator != null) {
            final ColumnMatcher matcher = MatcherRegistry.getMatcher(config.getColumnMatcher());
            if (matcher == null) {
                logger.error("Unable to find column matching strategy '" + config.getColumnMatcher() + "'");
            } else {
                ResultSetIterator<ComparableTuple> targetIterator = getAndRegisterResultSetIterator(comparison, matcher, config.getTargetQueryConfig());
                if (targetIterator != null) {
                    strategy.runComparison(sourceIterator, targetIterator, comparison, config);
                }
            }
        }
    }

    private ResultSetIterator<ComparableTuple> getAndRegisterResultSetIterator(ComparisonRuntime comparison, ColumnMatcher columnMatcher, QueryConfig sourceQueryConfig) {
        ResultSetIterator<ComparableTuple> rsIterator = getQueryIterator(sourceQueryConfig, comparison, columnMatcher);
        if (rsIterator == null) {
            return null;
        }
        if (!comparison.registerColumns(rsIterator.getColumns(), sourceQueryConfig, columnMatcher)) {
            return null;
        }
        return rsIterator;
    }

    private ResultSetIterator<ComparableTuple> getQueryIterator(final QueryConfig queryConfig,
                                                                final ComparisonRuntime comparison, final ColumnMatcher matcher) {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection(queryConfig.getDatabase());
        } catch (SQLException e) {
            logger.error("Unable to connect to " + queryConfig.getDatabase(), e);
            return null;
        }

        Function<ResultSet, ComparableTuple> tupleBuilder = (ResultSet rs) -> {
            Object[] values = extractObjectsFromRs(matcher, rs, queryConfig, comparison);
            return new ComparableTuple(values);
        };

        return new ResultSetIterator<>(connection, queryConfig.getStatement(), tupleBuilder, queryConfig.getFetchSize(), queryConfig.getQueryParameters());
    }

    /**
     * It is VERY important that we extract the elements from the RS in the SAME order than the one defined by the canonical columns!
     * As we are just returning an Object[], the column information on each value is lost. Keeping the order is the only way to match.
     * @param matcher
     * @param rs
     * @param queryConfig
     * @param comparison
     * @return
     */
    private static Object[] extractObjectsFromRs(ColumnMatcher matcher, ResultSet rs, QueryConfig queryConfig,
                                                 final ComparisonRuntime comparison) {
        String queryName = queryConfig.getName();
        List<String> canonicalColumns = comparison.getCanonicalColumns();
        List<String> providedColumns = comparison.getProvidedColumns(queryName);

        Object[] columnValues = new Object[canonicalColumns.size()];
        int columnIdx = 0;
        for (String canonicalColumn : canonicalColumns) {
            String column = matcher.getColumnFromCanonical(canonicalColumn, canonicalColumns, providedColumns);
            try {
                columnValues[columnIdx++] = rs.getObject(column);
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for column" + column + " for query " + queryName + "(canonical column was " + canonicalColumn + ")", e);
                return null;
            }

        }
        return columnValues;
    }


    //TODO: Nonsense here :'( Move parameter replacement to HProperties.
    //False: This have sense, in the context parameters are 'mapped' to JDBC parameters.
    // So we have 2 kind of 'parameters': The ones to be replaced directly in HProperties, and the ones to be passed to JDBC
    //Idea: Have a new query property (multivalued) with JDBC query parameters (that can be also HProperties parameters)
//    private static final String queryParamRegexp = "\\{(.*?)}";

//    private static Query buildQuery(final QueryConfig queryConfig) {
//
//        String queryStr = queryConfig.getStatement();
//        //final List<Object> queryParameterValues = prepareParameterValues(queryConfig);
//        final Object[] queryParameterValues = queryConfig.getQueryParameters();
//        // final String replacedQueryStr = replaceQueryParameterTags(queryStr);
//        try {
//            return new Query(queryStr, queryParameterValues, queryConfig.getFetchSize());
//        } catch (QueryUtilsException e) {
//            logger.error("Unable to build query " + queryConfig.getName(), e);
//            return null;
//        }
//    }

//    /**
//     * Replace parameters by '?' in order to be assigned in JDBC queries
//     *
//     * @param query String for the query
//     * @return Same query string, but with all parameter tags replaces by ?
//     */
//    private static String replaceQueryParameterTags(final String query) {
//
//        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
//        final Matcher parameterMatcher = parameterPattern.matcher(query);
//        return parameterMatcher.replaceAll("?");
//    }
//
//    /**
//     * Generate the list of parameter values, in the same order as they appear on the query.
//     * Parameter values are take from configuration properties.
//     *
//     * @param queryConfig The query string
//     * @return The list of values to be used for query parameters
//     */
//    private static List<Object> prepareParameterValues(final QueryConfig queryConfig) {
//        final List<Object> values = new ArrayList<>();
//        // 1) Store parameters in query order
//        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
//        final Matcher parameterMatcher = parameterPattern.matcher(queryConfig.getStatement());
//        while (parameterMatcher.find()) {
//            final String parameterTag = parameterMatcher.group();
//            final String parameterName = parameterTag.substring(1, parameterTag.length() - 1);
//
//            String parameterValue = queryConfig.getParameter(parameterName);
//
//            if (parameterTag.contains(ConfigurationLabels.DATE_PARAMETER_KEYWORD)) {
//                Date date = TaijituConfigImpl.parseDate(parameterValue);
//                if (date == null) {
//                    logger.error("Unable to parse date parameter '" + parameterName + "' for query '" + queryConfig.getName());
//                    return null;
//                }
//                values.add(date);
//            } else {
//                values.add(parameterValue);
//            }
//        }
//
//        return values;
//    }
}
