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

    //TODO: Move those methods to superclass
    @Override
    public List<EqualityConfig> getEqualityConfigs() {
        final List<ImmutableHierarchicalConfiguration> thisEqConfigs = configuration.immutableChildConfigurationsAt(Comparison.EQUALITY);
        final List<EqualityConfig> equalityConfigs = thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
        final List<EqualityConfig> parentEqualityConfigs = parent.getEqualityConfigs();
        equalityConfigs.addAll(parentEqualityConfigs);
        return equalityConfigs;
    }

}
