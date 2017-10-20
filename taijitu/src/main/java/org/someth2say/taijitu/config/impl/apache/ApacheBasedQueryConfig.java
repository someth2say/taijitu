package org.someth2say.taijitu.config.impl.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;

import java.util.List;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_QUERY_PARAMETERS;

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
    //TODO: Consider moving form Arrays to Lists
    default String[] getKeyFields() {
        String[] keys = getConfiguration().get(String[].class, Fields.KEY, null);
        return keys != null ? keys : getParent() != null ? getParent().getKeyFields() : null;
    }

    @Override
    default String getDatabaseRef() {
        String statement = getConfiguration().getString(Comparison.DATABASE_REF);
        return statement != null ? statement
                : getParent() != null ? getParent().getDatabaseRef()
                : getRefToFirstDbDefined();
    }

    private String getRefToFirstDbDefined() {
        final List<ImmutableHierarchicalConfiguration> dbConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.DATABASE);
        if (!dbConfigs.isEmpty()) {
            return dbConfigs.get(0).getRootElementName();
        }
        return null;
    }


    @Override
    default Object[] getQueryParameters() {
        final Object[] params = getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : getParent()!=null?getParent().getQueryParameters():DEFAULT_QUERY_PARAMETERS;
    }
}
