package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ComparisonPluginConfig;
import org.someth2say.taijitu.config.NamedConfig;

/**
 * Right now, plugins have no configuration, only name references
 */
public class ComparisonPluginConfigImpl2 extends NamedConfig implements ComparisonPluginConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    public ComparisonPluginConfigImpl2(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
    }
}
