package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.StrategyConfig;

public class ApacheStrategyConfigNode extends ApacheNamedConfigNode implements StrategyConfig {
    public ApacheStrategyConfigNode(ImmutableHierarchicalConfiguration strategyConfiguration) {
        super(strategyConfiguration,null);
    }
}
