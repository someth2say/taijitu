package org.someth2say.taijitu.query;

import org.someth2say.taijitu.query.database.IConnectionFactory;
import org.someth2say.taijitu.commons.StringUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jordi Sola
 */
public class Query {
    private static final String SELECT_ALL_FROM = "SELECT * FROM";

    private boolean queryOptimization = false;
    private IConnectionFactory connectionFactory;
    private String queryString;
    private List<Object> parameterValues = new ArrayList<>();
    private String queryName;
    private int fetchSize = 1024;
    private String[] columnNames;

    private String connectionName;

    public Query(String name, String queryStr, IConnectionFactory factory, String connectionName, final String[] fields, List<Object> values) throws QueryUtilsException {
        setQueryName(name);
        setConnectionName(connectionName);
        setQueryString(queryStr);
        setConnectionFactory(factory);
        setColumnNames(fields);
        setParameterValues(values);
        setQueryString(queryStr);
    }


    public Connection getConnection() throws QueryUtilsException {
        return connectionFactory.getConnection(connectionName);
    }

    private void setConnectionFactory(IConnectionFactory connectionFactory) throws QueryUtilsException {
        if (connectionFactory == null) {
            throw new QueryUtilsException("ConnectionFactory can not be null: " + getQueryName());
        }
        this.connectionFactory = connectionFactory;
    }

    public String getQueryString() {
        if (isQueryOptimization() && getQueryString().toUpperCase().startsWith(SELECT_ALL_FROM)) {
            return ("SELECT " + StringUtil.join(getColumnNames()) + " FROM").concat(queryString.substring(SELECT_ALL_FROM.length()));
        }
        return this.queryString;
    }

    public void setQueryString(String queryStr) throws QueryUtilsException {
        if (queryStr == null) {
            throw new QueryUtilsException("Query string can not be null: " + getQueryName());
        }

        this.queryString = queryStr;
    }

    public List<Object> getParameterValues() {
        return this.parameterValues;
    }

    public void setParameterValues(List<Object> _parameterValues) {
        this.parameterValues = _parameterValues;
    }

    public String getQueryName() {
        return this.queryName;
    }

    public void setQueryName(String _queryName) {
        this.queryName = _queryName;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(int _fetchSize) {
        this.fetchSize = _fetchSize;
    }

    public String[] getColumnNames() {
        return columnNames != null ? columnNames.clone() : null;
    }

    public void setColumnNames(String[] _columnNames) {
        this.columnNames = _columnNames;
    }

    public boolean isQueryOptimization() {
        return queryOptimization;
    }

    public void setQueryOptimization(boolean queryOptimization) {
        this.queryOptimization = queryOptimization;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

}
