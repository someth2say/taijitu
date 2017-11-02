package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheConfig;
import org.someth2say.taijitu.config.apache.defaults.ApacheNamedConfig;

public abstract class ApacheNamed<P extends ApacheConfig> extends Apache<P> implements ApacheNamedConfig {

    public ApacheNamed(ImmutableHierarchicalConfiguration configuration, P parent) {
        super(configuration, parent);
    }
}
