package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedQuerySourceConfig;

public class QuerySourceConfigImpl extends NamedConfig implements ApacheBasedQuerySourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final QuerySourceConfig parent;

    public QuerySourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                                 final QuerySourceConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    @Override
    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public QuerySourceConfig getParent() {
        return parent;
    }

    private DatabaseConfig databaseConfig;

    @Override
    public DatabaseConfig getDatabaseConfig() {
        if (databaseConfig == null) {
            databaseConfig = ApacheBasedQuerySourceConfig.super.getDatabaseConfig();
        }
        return databaseConfig;
    }
}
