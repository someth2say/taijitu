package org.someth2say.taijitu.cli.source.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.DefaultConfig;
import org.someth2say.taijitu.cli.source.AbstractSource;
import org.someth2say.taijitu.cli.source.FieldDescription;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class QuerySource extends AbstractSource<ResultSet> {
    private static final Logger logger = LoggerFactory.getLogger(QuerySource.class);

    private final Connection connection;
    private final FetchData fetchData;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private List<FieldDescription<?>> providedFields;

    public QuerySource(String name, Properties buildProperties, Properties fetchProperties) throws SQLException {
        super(name, buildProperties, fetchProperties);
        this.connection = ConnectionManager.getConnection(buildProperties);
        this.fetchData = new FetchData(fetchProperties);
}

    private static class FetchData {
        private final String statement;
        private final int fetchSize;

        FetchData(Properties fetchProperties) {
            this.statement = fetchProperties.getProperty(ConfigurationLabels.STATEMENT);
            String fetchSizeProp = fetchProperties.getProperty(ConfigurationLabels.FETCH_SIZE);

            int fs;
            try {
                fs = Integer.parseInt(fetchSizeProp);
            } catch (NumberFormatException e) {
                fs = DefaultConfig.DEFAULT_FETCH_SIZE;
            }
            this.fetchSize = fs;

        }

        String getStatement() {
            return statement;
        }

        int getFetchSize() {
            return fetchSize;
        }

    }

    private void init() {
        try {
            this.preparedStatement = getPreparedStatement();
            this.resultSet = preparedStatement.executeQuery();
            this.providedFields = buildFieldDescriptions();
        } catch (SQLException | ClassNotFoundException e) {
            close();
        }
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(fetchData.getStatement());
        preparedStatement.setFetchSize(fetchData.getFetchSize());
        return preparedStatement;
    }

    private ArrayList<FieldDescription<?>> buildFieldDescriptions() throws ClassNotFoundException {
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rsColumnCount = resultSetMetaData.getColumnCount();
            ArrayList<FieldDescription<?>> result = new ArrayList<>(rsColumnCount);
            for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
                String columnName = resultSetMetaData.getColumnName(columnIdx);
                final String columnClassName = resultSetMetaData.getColumnClassName(columnIdx);
                result.add(new FieldDescription<>(columnName, Class.forName(columnClassName)));
            }
            return result;
        } catch (SQLException e) {
            logger.error("Unable to extract columns from ResultSet metadata.", e);
            return null;
        }
    }

    @Override
	public void close() {
        try {
            preparedStatement.close(); // This also closes the explain-set
        } catch (SQLException e) {
            //nothing we can do here
        }
        try {
            connection.close();
        } catch (SQLException e) {
            //nothing we can do here
        }
    }

    @Override
    public List<FieldDescription<?>> getProvidedFields() {
        if (preparedStatement == null) {
            init();
        }
        return providedFields;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <V> Function<ResultSet, V> getExtractor(FieldDescription<V> fd) {
        return (ResultSet rs) -> {
            try {
                return (V) rs.getObject(fd.getName());
            } catch (SQLException e) {
                throw new RuntimeException("Can't get field " + fd + "from ResultSet", e);
            }
        };
    }

    @Override
    public Stream<ResultSet> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(buildIterator(), 0), false);
    }

    private Iterator<ResultSet> buildIterator() {

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
            public ResultSet next() {
                try {
                    return resultSet;
                } catch (Exception e) {
                    close();
                    throw e;
                }
            }
        };
    }

}