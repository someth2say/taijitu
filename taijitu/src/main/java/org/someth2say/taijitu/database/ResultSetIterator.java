package org.someth2say.taijitu.database;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.ResultSetTupleBuilder;
import org.someth2say.taijitu.util.ImmutablePair;

import java.sql.*;
import java.util.Iterator;

public class ResultSetIterator implements Iterator<ComparableTuple> {
    private static final Logger logger = Logger.getLogger(ResultSetIterator.class);

    //TODO: Considering adding an the last exception raised, so we can check the status.
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private String sql;
    private ResultSetTupleBuilder builder;
    private int fetchSize;
    private Object[] parameters;

    public ResultSetIterator(Connection connection, String sql, ResultSetTupleBuilder builder, final int fetchSize, final Object[] parameters) {
        this.fetchSize = fetchSize;
        this.parameters = parameters;
        assert connection != null;
        assert sql != null;
        this.builder = builder;
        this.connection = connection;
        this.sql = sql;
    }

    private void init() {
        try {
            preparedStatement = getPreparedStatement();
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            close();
        }
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setFetchSize(fetchSize);
        for (int paramIdx = 0; paramIdx < parameters.length; paramIdx++) {
            Object object = parameters[paramIdx];
            if (object instanceof java.util.Date) {
                preparedStatement.setDate(paramIdx + 1, new Date(((java.util.Date) object).getTime()));
            } else {
                preparedStatement.setObject(paramIdx + 1, object);
            }
        }
        return preparedStatement;
    }


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
    public ComparableTuple next() {
        try {
            //TODO: Built tuple should also include the comparators!
            return builder.apply(resultSet);
        } catch (Exception e) {
            close();
            throw e;
        }
    }


    /**
     * Return the column names, as provided by resultSet metadata.
     *
     * @return Array with description for each column.
     */
    // TODO: Create `FieldDescription` class
    public FieldDescription[] getColumns() {
        if (preparedStatement == null) {
            init();
        }

        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rsColumnCount = resultSetMetaData.getColumnCount();
            FieldDescription[] result = new FieldDescription[rsColumnCount];
            for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
                String columnName = resultSetMetaData.getColumnName(columnIdx);
                final String columnClassName = resultSetMetaData.getColumnClassName(columnIdx);

                result[columnIdx - 1] = new FieldDescription(columnName, columnClassName);

            }
            return result;
        } catch (SQLException e) {
            logger.error("Unable to extract columns from ResultSet metadata.", e);
            return null;
        }
    }

}