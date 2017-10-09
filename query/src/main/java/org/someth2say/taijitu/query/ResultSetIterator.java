package org.someth2say.taijitu.query;

import java.sql.*;
import java.util.Iterator;
import java.util.function.Function;

public class ResultSetIterator<T> implements Iterator<T> {

    private ResultSet rs;
    private PreparedStatement ps;
    private Connection connection;
    private String sql;
    private Function<ResultSet, T> builder;

    public ResultSetIterator(Connection connection, String sql, Function<ResultSet, T> builder) {
        assert connection != null;
        assert sql != null;
        this.builder = builder;
        this.connection = connection;
        this.sql = sql;
    }

    public void init() {
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

        } catch (SQLException e) {
            close();
            throw e;
        }
    }

    @Override
    public boolean hasNext() {
        if (ps == null) {
            init();
        }
        try {
            boolean hasMore = rs.next();
            if (!hasMore) {
                close();
            }
            return hasMore;
        } catch (SQLException e) {
            close();
//            throw new DataAccessException(e);
        }

    }

    private void close() {
        try {
            rs.close();
            try {
                ps.close();
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
            return builder.apply(rs);
        } catch (Exception e) {
            close();
            throw e;
        }
    }


    /**
     * Return the column names, as provided by rs metadata.
     * @return
     * @throws SQLException
     */
    // TODO: Maybe some day will be worth returning more information (i.e. column type).
    public String[] getColumns() throws SQLException {
        if (ps == null) {
            init();
        }

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        int rsColumnCount = resultSetMetaData.getColumnCount();
        String[] result = new String[rsColumnCount];
        for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
            String columnName = resultSetMetaData.getColumnName(columnIdx);
            if (columnName != null && !"".equals(columnName)) {
                result[columnIdx - 1] = columnName;
            }
        }
        return result;
    }


}