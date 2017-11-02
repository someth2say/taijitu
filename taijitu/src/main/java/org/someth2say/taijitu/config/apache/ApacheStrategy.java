package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheStrategyConfig;


public class ApacheStrategy extends ApacheNamed<ApacheStrategyConfig> implements ApacheStrategyConfig {

    public ApacheStrategy(ImmutableHierarchicalConfiguration configuration, ApacheStrategyConfig parent) {
        super(configuration, parent);
    }
}
