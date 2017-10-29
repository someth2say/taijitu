package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.SourceConfig;

import java.util.List;

public abstract class SourceConfigImpl extends NamedConfig implements SourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final SourceConfig parent;

    public SourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                            final SourceConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public SourceConfig getParent() {
        return parent;
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
