package org.someth2say.taijitu.query;

/**
 * @author Jordi Sola
 */
@Deprecated
public class Query {
    private final String queryString;
    private final Object[] parameterValues;
    private final int fetchSize;

    public Query(final String queryStr, final Object[] values, final int fetchSize) throws QueryUtilsException {
        if (queryStr == null) {
            throw new QueryUtilsException("Query string can not be null");
        }

        this.queryString = queryStr;
        this.parameterValues = values;
        this.fetchSize = fetchSize;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public Object[] getParameterValues() {
        return this.parameterValues;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

}
