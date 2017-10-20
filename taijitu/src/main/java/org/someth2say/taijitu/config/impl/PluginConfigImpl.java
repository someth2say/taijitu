package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedPluginConfig;

/**
 * Right now, plugins have no configuration, only name references
 */
public class PluginConfigImpl extends NamedConfig implements ApacheBasedPluginConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    public PluginConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
    }

    @Override
    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }
}
