package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.PluginConfig;

public class EqualityConfigImpl extends NamedConfig implements EqualityConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    public EqualityConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        //TODO: This only works because we are using the schema "..equality.name=name"
        super(configuration.getRootElementName());
        this.configuration = configuration.immutableConfigurationAt(getName());
    }

    @Override
    public String getFieldName() {
        return configuration.getString(ConfigurationLabels.Comparison.FIELD_NAME);
    }

    @Override
    public String getFieldClass() {
        return configuration.getString(ConfigurationLabels.Comparison.FIELD_CLASS);
    }

    @Override
    public String getEqualityParameters() {
        return configuration.getString(ConfigurationLabels.Comparison.EQUALITY_PARAMS);
    }
}
