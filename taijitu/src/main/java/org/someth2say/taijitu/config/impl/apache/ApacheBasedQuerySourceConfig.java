package org.someth2say.taijitu.config.impl.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.impl.DatabaseConfigImpl;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_QUERY_PARAMETERS;

public interface ApacheBasedQuerySourceConfig extends ApacheBasedSourceConfig, QuerySourceConfig {

    @Override
	QuerySourceConfig getParent();

    @Override
    default String getStatement() {
        String statement = getConfiguration().getString(Comparison.STATEMENT);
        return statement != null ? statement : getParent() != null ? getParent().getStatement() : null;
    }

    @Override
    default int getFetchSize() {
        Integer fs = getConfiguration().getInteger(Setup.FETCH_SIZE, null);
        return fs != null ? fs : getParent() != null ? getParent().getFetchSize() : DEFAULT_FETCHSIZE;
    }

    @Override
    default Object[] getQueryParameters() {
        final Object[] params = getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : getParent() != null ? getParent().getQueryParameters() : DEFAULT_QUERY_PARAMETERS;
    }

    @Override
    default DatabaseConfig getDatabaseConfig() {
        try {
            final ImmutableHierarchicalConfiguration databaseConfig = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Sections.DATABASE);
            return new DatabaseConfigImpl(databaseConfig);
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            return getParent() != null ? getParent().getDatabaseConfig() : null;
        }
    }
}