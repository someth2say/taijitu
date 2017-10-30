package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegate.StrategyConfigDelegate;

public class ApacheStrategyConfig extends ApacheNamedConfig implements StrategyConfigDelegate {
    public ApacheStrategyConfig(ImmutableHierarchicalConfiguration strategyConfiguration) {
        super(strategyConfiguration);
    }
}
