package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.delegate.SourceConfigDelegate;

import java.util.List;
import java.util.Properties;

public class ApacheSourceConfig extends ApacheNamedConfig implements SourceConfigDelegate {

    ApacheSourceConfig(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getType() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
    }

    @Override
    public List<String> getKeyFields() {
        //TODO: Consider '*' wildcard for key fields
        return getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
    }

    @Override
    public Properties getSourceProperties() {
        //TODO: Investigate how to structure source properties (i.e. databaseProperties) so they can provided by parents
        return getConfiguration().getProperties(ConfigurationLabels.Comparison.SOURCE_PROPERTIES);
    }

}
