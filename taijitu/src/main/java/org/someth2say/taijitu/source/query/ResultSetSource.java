package org.someth2say.taijitu.source.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ResultSetSource implements Source {
    private static final Logger logger = Logger.getLogger(ResultSetSource.class);
    public static final String NAME = "query";

    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private final IComparisonCfg iComparison;
    private final ComparisonContext context;
    private final FetchProperties fetchProperties;
    private final ISourceCfg iSource;

    @Override
    public ISourceCfg getConfig() {
        return iSource;
    }

    private ResultSetTupleBuilder builder;


    private static class FetchProperties {
        private final String statement;
        private final int fetchSize;
        private final List<Object> queryParameters;

        FetchProperties(Properties properties) {
            this.statement = properties.getProperty(ConfigurationLabels.Comparison.STATEMENT);
            String property = properties.getProperty(ConfigurationLabels.Setup.FETCH_SIZE);

            int fs;
            try {
                fs = Integer.parseInt(property);
            } catch (NumberFormatException e) {
                fs = DefaultConfig.DEFAULT_FETCHSIZE;
            }
            this.fetchSize = fs;

            String qp = properties.getProperty(ConfigurationLabels.Comparison.QUERY_PARAMETERS);
            if (qp != null) {
                this.queryParameters = List.of(StringUtils.split(qp, DefaultConfig.DEFAULT_LIST_DELIMITER));
            } else {
                this.queryParameters = Collections.emptyList();
            }
        }

        String getStatement() {
            return statement;
        }

        int getFetchSize() {
            return fetchSize;
        }

        List<Object> getQueryParameters() {
            return queryParameters;
        }

    }


    public ResultSetSource(final ISourceCfg iSource, final IComparisonCfg iComparison, final ComparisonContext context) throws SQLException {
        this.iComparison = iComparison;
        this.context = context;
        this.iSource = iSource;
        this.fetchProperties = new FetchProperties(iSource.getFetchProperties());
        this.connection = ConnectionManager.getConnection(iSource.getBuildProperties());
        assert connection != null;
    }


    private PreparedStatement getPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(fetchProperties.getStatement());
        preparedStatement.setFetchSize(fetchProperties.getFetchSize());
        assignSqlParameters(preparedStatement);
        return preparedStatement;
    }

    private void assignSqlParameters(PreparedStatement preparedStatement) throws SQLException {
        //TODO: Here we lost type-safety because we went through Properties... consider using a `Map<String,Object>` instead.
        List<Object> sqlParameters = fetchProperties.getQueryParameters();
        for (int paramIdx = 0; paramIdx < sqlParameters.size(); paramIdx++) {
            Object object = sqlParameters.get(paramIdx);
            if (object instanceof java.util.Date) {
                preparedStatement.setDate(paramIdx + 1, new Date(((java.util.Date) object).getTime()));
            } else {
                preparedStatement.setObject(paramIdx + 1, object);
            }
        }
    }

    private void init() {
        try {
            preparedStatement = getPreparedStatement();
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            close();
        }
    }


    public void close() {
        try {
            resultSet.close();
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                //nothing we can do here
            }
        } catch (SQLException e) {
            //nothing we can do here
        }
    }

    @Override
    public List<FieldDescription> getFieldDescriptions() {
        if (preparedStatement == null) {
            init();
        }

        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rsColumnCount = resultSetMetaData.getColumnCount();
            ArrayList<FieldDescription> result = new ArrayList<>(rsColumnCount);
            for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
                String columnName = resultSetMetaData.getColumnName(columnIdx);
                final String columnClassName = resultSetMetaData.getColumnClassName(columnIdx);

                result.add(new FieldDescription(columnName, columnClassName));

            }
            return result;
        } catch (SQLException e) {
            logger.error("Unable to extract columns from ResultSet metadata.", e);
            return null;
        }
    }

    @Override
    public Iterator<ComparableTuple> iterator() {
        return new Iterator<ComparableTuple>() {

            @Override
            public boolean hasNext() {
                if (preparedStatement == null) {
                    init();
                }
                try {
                    boolean hasMore = resultSet.next();
                    if (!hasMore) {
                        close();
                    }
                    return hasMore;
                } catch (SQLException e) {
                    close();
                    return false;
                }
            }


            @Override
            public ComparableTuple next() {
                try {
                    return getTupleBuilder().apply(resultSet);
                } catch (Exception e) {
                    close();
                    throw e;
                }
            }
        };
    }

    private TupleBuilder<ResultSet> getTupleBuilder() {
        if (builder == null) {
            final FieldMatcher matcher = MatcherRegistry.getMatcher(iComparison.getMatchingStrategyName());
            builder = new ResultSetTupleBuilder(matcher, context, iSource.getName());
        }
        return builder;
    }

    @Override
    public String getName() {
        return ResultSetSource.NAME;
    }
}