package org.someth2say.taijitu.config.impl.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;
import org.someth2say.taijitu.config.impl.DatabaseConfigImpl;

import java.util.List;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_QUERY_PARAMETERS;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_STRATEGY_CONFIG;

public interface ApacheBasedQueryConfig extends ApacheBasedConfig, QueryConfig {

    QueryConfig getParent();

    @Override
    default String getStatement() {
        String statement = getConfiguration().getString(Comparison.STATEMENT);
        return statement != null ? statement : getParent().getStatement();
    }

    @Override
    default int getFetchSize() {
        Integer fs = getConfiguration().getInteger(Setup.FETCH_SIZE, null);
        return fs != null ? fs : getParent() != null ? getParent().getFetchSize() : DEFAULT_FETCHSIZE;
    }

    @Override
    default List<String> getKeyFields() {
        List<String> keys = getConfiguration().getList(String.class, Fields.KEY, null);
        return keys != null ? keys : getParent() != null ? getParent().getKeyFields() : null;
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