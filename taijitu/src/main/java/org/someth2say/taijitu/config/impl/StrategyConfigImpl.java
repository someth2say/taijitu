package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.StrategyConfig;

public class StrategyConfigImpl extends NamedConfig implements StrategyConfig {
    private final ImmutableHierarchicalConfiguration configuration;

    public StrategyConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getString("")); // Strategy name is directly a value in the configuration root!
        this.configuration = configuration;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }
}
