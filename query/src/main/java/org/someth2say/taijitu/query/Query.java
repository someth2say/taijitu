package org.someth2say.taijitu.query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jordi Sola
 */
public class Query {
    private final String queryString;
    private final List<Object> parameterValues;
    private final int fetchSize;

    public Query(final String queryStr, final List<Object> values, final int fetchSize) throws QueryUtilsException {
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

    public List<Object> getParameterValues() {
        return this.parameterValues;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

}
