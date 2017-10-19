package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.QueryConfig;

import java.util.List;
import java.util.stream.Collectors;

public class QueryConfigImpl extends NamedConfig implements QueryConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final QueryConfig parent;

    public QueryConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                           final QueryConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    @Override
    public String getStatement() {
        String statement = getConfiguration().getString(Comparison.STATEMENT);
        return statement != null ? statement : getParent().getStatement();
    }

    @Override
    public int getFetchSize() {
        Integer fs = getConfiguration().getInteger(Setup.FETCH_SIZE, null);
        return fs != null ? fs : getParent().getFetchSize();
    }

    @Override
    public String[] getKeyFields() {
        String[] keys = getConfiguration().get(String[].class, Fields.KEY, null);
        return keys != null ? keys : getParent().getKeyFields();
    }

    @Override
    public String getDatabaseRef() {
        String statement = getConfiguration().getString(Comparison.DATABASE_REF);
        return statement != null ? statement : getParent().getDatabaseRef();
    }

    @Override
    public Object[] getQueryParameters() {
        final Object[] params = getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : getParent().getQueryParameters();
    }

	public ImmutableHierarchicalConfiguration getConfiguration() {
		return configuration;
	}

	public QueryConfig getParent() {
		return parent;
	}

}
