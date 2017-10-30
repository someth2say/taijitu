package org.someth2say.taijitu.source;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.database.ConnectionManager;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.builder.ResultSetTupleBuilder;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultSetSource implements Source {
    private static final Logger logger = Logger.getLogger(ResultSetSource.class);
    public static final String NAME = "query";

    //TODO: Considering adding an the last exception raised, so we can check the status.
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private final ComparisonConfigIface comparisonConfigIface;
    private final ComparisonContext context;

    @Override
    public QuerySourceConfigIface getConfig() {
        return querySourceConfigIface;
    }

    private final QuerySourceConfigIface querySourceConfigIface;
    private ResultSetTupleBuilder builder;

    public ResultSetSource(final QuerySourceConfigIface querySourceConfigIface, final ComparisonConfigIface comparisonConfigIface, final ComparisonContext context) throws SQLException {
        this.querySourceConfigIface = querySourceConfigIface;
        assert this.querySourceConfigIface != null;

        this.comparisonConfigIface = comparisonConfigIface;
        this.context = context;

        this.connection = ConnectionManager.getConnection(this.querySourceConfigIface.getDatabaseConfig());
        assert connection != null;
    }


    private PreparedStatement getPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(querySourceConfigIface.getStatement());
        preparedStatement.setFetchSize(querySourceConfigIface.getFetchSize());
        assignSqlParameters(preparedStatement);
        return preparedStatement;
    }

    private void assignSqlParameters(PreparedStatement preparedStatement) throws SQLException {
        Object[] sqlParameters = querySourceConfigIface.getQueryParameters();
        for (int paramIdx = 0; paramIdx < sqlParameters.length; paramIdx++) {
            Object object = sqlParameters[paramIdx];
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


    private void close() {
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
        return new Iterator<>() {

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
            final FieldMatcher matcher = MatcherRegistry.getMatcher(comparisonConfigIface.getMatchingStrategyName());
            builder = new ResultSetTupleBuilder(matcher, context, querySourceConfigIface.getName());
        }
        return builder;
    }

    @Override
    public String getName() {
        return ResultSetSource.NAME;
    }
}