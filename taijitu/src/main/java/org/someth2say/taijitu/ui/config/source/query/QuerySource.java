package org.someth2say.taijitu.ui.config.source.query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.ui.config.ConfigurationLabels;
import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.ui.config.source.AbstractSource;
import org.someth2say.taijitu.ui.config.source.FieldDescription;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class QuerySource extends AbstractSource<ResultSet> {
    private static final Logger logger = LoggerFactory.getLogger(QuerySource.class);
    public static final String NAME = "query";

    private final Connection connection;

    //TODO: this properties should be created externally to the source
    private final FetchProperties fetchProperties;

    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private List<FieldDescription<?>> providedFields;

    public QuerySource(final ISourceCfg iSource) throws SQLException {
        super(iSource);
        this.fetchProperties = new FetchProperties(iSource.getFetchProperties());
        this.connection = ConnectionManager.getConnection(iSource.getBuildProperties());
    }

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
                this.queryParameters = Arrays.asList(StringUtils.split(qp, DefaultConfig.DEFAULT_LIST_DELIMITER));
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


    private void init() {
        try {
            this.preparedStatement = getPreparedStatement();
            this.resultSet = preparedStatement.executeQuery();
            this.providedFields = buildFieldDescriptions();

        } catch (SQLException e) {
            close();
        }
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(fetchProperties.getStatement());
        preparedStatement.setFetchSize(fetchProperties.getFetchSize());
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
        return preparedStatement;
    }

    private ArrayList<FieldDescription<?>> buildFieldDescriptions() {
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rsColumnCount = resultSetMetaData.getColumnCount();
            ArrayList<FieldDescription<?>> result = new ArrayList<>(rsColumnCount);
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
    public Class<ResultSet> getTypeParameter() {
        return ResultSet.class;
    }

    @Override
    public List<FieldDescription<?>> getProvidedFields() {
        if (preparedStatement == null) {
            init();
        }
        return providedFields;
    }

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

        return new Iterator<ResultSet>() {

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