package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.StrategyConfig;

public class StrategyConfigImpl2 extends NamedConfig implements StrategyConfig {
    private final ImmutableHierarchicalConfiguration configuration;

    public StrategyConfigImpl2(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration.immutableConfigurationAt(getName());
    }
}
