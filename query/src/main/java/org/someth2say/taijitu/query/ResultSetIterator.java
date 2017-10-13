package org.someth2say.taijitu.query;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;

public class ResultSetIterator<T> implements Iterator<T> {
    private static final Logger logger = Logger.getLogger(ResultSetIterator.class);

    //TODO: Considering adding an the last exception raised, so we can check the status.
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private String sql;
    private Function<ResultSet, T> builder;
    private int fetchSize;
    private Object[] parameters;

    public ResultSetIterator(Connection connection, String sql, Function<ResultSet, T> builder, final int fetchSize, final Object[] parameters) {
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
    public T next() {
        try {
            return builder.apply(resultSet);
        } catch (Exception e) {
            close();
            throw e;
        }
    }


    /**
     * Return the column names, as provided by resultSet metadata.
     *
     * @return
     * @throws SQLException
     */
    // TODO: Maybe some day will be worth returning more information (i.e. column type).
    public String[] getColumns() {
        if (preparedStatement == null) {
            init();
        }

        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rsColumnCount = resultSetMetaData.getColumnCount();
            String[] result = new String[rsColumnCount];
            for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
                String columnName = resultSetMetaData.getColumnName(columnIdx);
                if (columnName != null && !"".equals(columnName)) {
                    result[columnIdx - 1] = columnName;
                }
            }
            return result;
        } catch (SQLException e) {
            logger.error("Unable to extract columns from ResultSet metadata.", e);
            return null;
        }
    }


}