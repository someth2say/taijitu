package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.SourceConfig;

import java.util.List;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_QUERY_PARAMETERS;

public class QuerySourceConfigImpl extends SourceConfigImpl implements QuerySourceConfig, SourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final QuerySourceConfig parent;

    public QuerySourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                                 final QuerySourceConfig parent) {
        super(configuration, parent);
        this.configuration = configuration;
        this.parent = parent;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public QuerySourceConfig getParent() {
        return parent;
    }

    private DatabaseConfig databaseConfig;

    //@Override
    public DatabaseConfig getMemoizedDatabaseConfig() {
        if (databaseConfig == null) {
            databaseConfig = getDatabaseConfig();
        }
        return databaseConfig;
    }

    @Override
    public String getStatement() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.STATEMENT);
        return statement != null ? statement : getParent() != null ? getParent().getStatement() : null;
    }

    @Override
    public int getFetchSize() {
        Integer fs = getConfiguration().getInteger(ConfigurationLabels.Setup.FETCH_SIZE, null);
        return fs != null ? fs : getParent() != null ? getParent().getFetchSize() : DEFAULT_FETCHSIZE;
    }

    @Override
    public Object[] getQueryParameters() {
        final Object[] params = getConfiguration().get(Object[].class, ConfigurationLabels.Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : getParent() != null ? getParent().getQueryParameters() : DEFAULT_QUERY_PARAMETERS;
    }

    @Override
    public DatabaseConfig getDatabaseConfig() {
        try {
            final ImmutableHierarchicalConfiguration databaseConfig = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Sections.DATABASE);
            return new DatabaseConfigImpl(databaseConfig);
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            return getParent() != null ? getParent().getDatabaseConfig() : null;
        }
    }

    @Override
    public List<String> getKeyFields() {
        List<String> keys = getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
        return keys != null ? keys : getParent() != null ? getParent().getKeyFields() : null;
    }

    @Override
    public String getType() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
        return statement != null ? statement : getParent() != null ? getParent().getType() : null;
    }
}
