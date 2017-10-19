package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;

public interface ApacheBasedQueryConfig extends ApacheBasedConfig, QueryConfig, DelegatingConfig<QueryConfig>{

	    default public String getStatement() {
	        String statement = getConfiguration().getString(Comparison.STATEMENT);
	        return statement != null ? statement : getParent().getStatement();
	    }

	    default public int getFetchSize() {
	        Integer fs = getConfiguration().getInteger(Setup.FETCH_SIZE, null);
	        return fs != null ? fs : getParent().getFetchSize();
	    }

	    default public String[] getKeyFields() {
	        String[] keys = getConfiguration().get(String[].class, Fields.KEY, null);
	        return keys != null ? keys : getParent().getKeyFields();
	    }

	    default public String getDatabaseRef() {
	        String statement = getConfiguration().getString(Comparison.DATABASE_REF);
	        return statement != null ? statement : getParent().getDatabaseRef();
	    }

	    default public Object[] getQueryParameters() {
	        final Object[] params = getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, null);
	        return params != null ? params : getParent().getQueryParameters();
	    }
}
