package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;

public class ApacheQuerySourceConfigNode extends ApacheSourceConfigNode implements QuerySourceConfig {
    ApacheQuerySourceConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheComparisonConfigNode parent) {
        super(configuration, parent);
    }

    @Override
    public String getStatement() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.STATEMENT);
    }

    @Override
    public int getFetchSize() {
        return getConfiguration().getInteger(ConfigurationLabels.Setup.FETCH_SIZE, null);
    }

    @Override
    public Object[] getQueryParameters() {
        return getConfiguration().get(Object[].class, ConfigurationLabels.Comparison.QUERY_PARAMETERS, null);
    }

    @Override
    public DatabaseConfig getDatabaseConfig() {
        try {
            final ImmutableHierarchicalConfiguration databaseConfig = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Sections.DATABASE);
            return new ApacheDatabaseConfigNode(databaseConfig,this);
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            return null;
        }
    }
}
