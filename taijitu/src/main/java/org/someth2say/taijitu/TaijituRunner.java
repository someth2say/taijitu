package org.someth2say.taijitu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.matcher.ColumnMatcher;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.ResultSetIterator;
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

        try {
            ComparisonRuntime comparison = new ComparisonRuntime(config);

            result.setStatus(ComparisonResult.ComparisonResultStatus.RUNNING);

            Map<ComparisonPluginConfig, TaijituPlugin> plugins = PluginRegistry
                    .getPlugins(config.getComparisonPluginConfigs());

            runPluginsPreComparison(comparison, plugins);

            runComparisonStrategy(comparison);

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

    private void runComparisonStrategy(ComparisonRuntime comparison) throws TaijituException {
        // Show comparison description
        logger.info("COMPARISON: " + config.getName() + "(strategy " + config.getStrategyConfig().getName() + ")");
        logger.debug("PARAMETERS: " + config.getAllParameters());


        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(config.getStrategyConfig());
        ResultSetIterator<ComparableTuple> sourceIterator = getQueryIterator(config.getSourceQueryConfig(), comparison);
        ResultSetIterator<ComparableTuple> targetIterator = getQueryIterator(config.getTargetQueryConfig(), comparison);

        try {
            String[] sourceColumns = sourceIterator.getColumns();
            comparison.registerColumns(sourceColumns, config.getSourceQueryConfig());

            String[] targetColumns = targetIterator.getColumns();
            comparison.registerColumns(targetColumns, config.getTargetQueryConfig());

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (QueryUtilsException e) {
            e.printStackTrace();
        }

        strategy.runComparison(sourceIterator, targetIterator, comparison, config);
    }

    private ResultSetIterator<ComparableTuple> getQueryIterator(final QueryConfig queryConfig, final ComparisonRuntime comparison) throws TaijituException {
        final ColumnMatcher columnMatcher = MatcherRegistry.getMatcher(config.getColumnMatcher());
        Function<ResultSet, ComparableTuple> builder = (ResultSet rs) -> new ComparableTuple(extractObjectsFromRs(columnMatcher, rs, queryConfig, comparison));
        String sql = buildQuery(queryConfig).getQueryString();
        Connection connection = getConnection(queryConfig.getDatabase());
        return new ResultSetIterator<>(connection, sql, builder, fetchSize, parameters);
    }

    public static Object[] extractObjectsFromRs(ColumnMatcher matcher, ResultSet rs, QueryConfig queryConfig, final ComparisonRuntime comparison) {
        List<String> canonicalColumns = comparison.getCanonicalColumns();
        Object[] columnValues = new Object[canonicalColumns.size()];
        int columnIdx = 0;
        for (String canonicalColumn : canonicalColumns) {
            String column = matcher.getReverseMatchingColumn(canonicalColumn, comparison.getCanonicalColumns(), comparison.getProvidedColumns(queryConfig.getName()));
            try {
                columnValues[columnIdx++] = rs.getObject(column);
            } catch (SQLException e) {
                //throw new QueryUtilsException("Can\'t retrieve column value for " + columns[columnIdx], e);
                logger.fatal("Can\'t retrieve value for column" + column + " for query " + queryConfig.getName() + "(canonical column was " + canonicalColumn + ")", e);
            }
        }
        return columnValues;
    }


    //TODO: Nonsense here :'( Move parameter replacement to HProperties.
    private static final String queryParamRegexp = "\\{(.*?)}";

    private static Query buildQuery(final QueryConfig queryConfig) throws TaijituException {

        String queryStr = queryConfig.getStatement();
        final List<Object> queryParameterValues = prepareParameterValues(queryConfig);
        final String replacedQueryStr = replaceQueryParameterTags(queryStr);

        try {
            return new Query(replacedQueryStr, queryParameterValues, queryConfig.getFetchSize());
        } catch (QueryUtilsException e) {
            throw new TaijituException("Unable to build query " + queryConfig.getName(), e);
        }

    }

    /**
     * Replace parameters by '?' in order to be assigned in JDBC queries
     *
     * @param query String for the query
     * @return Same query string, but with all parameter tags replaces by ?
     */
    private static String replaceQueryParameterTags(final String query) {

        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
        final Matcher parameterMatcher = parameterPattern.matcher(query);
        return parameterMatcher.replaceAll("?");
    }

    /**
     * Generate the list of parameter values, in the same order as they appear on the query.
     * Parameter values are take from configuration properties.
     *
     * @param queryConfig The query string
     * @return The list of values to be used for query parameters
     */
    private static List<Object> prepareParameterValues(final QueryConfig queryConfig) throws TaijituException {
        final List<Object> values = new ArrayList<>();
        // 1) Store parameters in query order
        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
        final Matcher parameterMatcher = parameterPattern.matcher(queryConfig.getStatement());
        while (parameterMatcher.find()) {
            final String parameterTag = parameterMatcher.group();
            final String parameterName = parameterTag.substring(1, parameterTag.length() - 1);

            String parameterValue = queryConfig.getParameter(parameterName);

            if (parameterTag.contains(ConfigurationLabels.DATE_PARAMETER_KEYWORD)) {
                values.add(TaijituConfigImpl.parseDate(parameterValue));
            } else {
                values.add(parameterValue);
            }
        }

        return values;
    }
}
