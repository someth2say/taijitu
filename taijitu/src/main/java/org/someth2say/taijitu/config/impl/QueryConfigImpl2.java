package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.QueryConfig;

import java.util.List;

public class QueryConfigImpl2 extends NamedConfig implements QueryConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final QueryConfig parent;

    public QueryConfigImpl2(final ImmutableHierarchicalConfiguration configuration,
                            final QueryConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration.immutableConfigurationAt(getName());
        this.parent = parent;
    }

    @Override
    public String getStatement() {
        String statement = configuration.getString(Comparison.STATEMENT);
        return statement != null ? statement : parent.getStatement();
    }

    @Override
    public int getFetchSize() {
        Integer fs = configuration.getInteger(Setup.FETCH_SIZE, null);
        return fs != null ? fs : parent.getFetchSize();
    }

    @Override
    public String[] getKeyFields() {
        String[] keys = configuration.get(String[].class, Fields.KEY, null);
        return keys != null ? keys : parent.getKeyFields();
    }

    @Override
    public String getDatabaseRef() {
        String statement = configuration.getString(Comparison.DATABASE_REF);
        return statement != null ? statement : parent.getDatabaseRef();
    }

    @Override
    public Object[] getQueryParameters() {
        final Object[] params = configuration.get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : parent.getQueryParameters();
    }

}
