package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheConfig;

public abstract class Apache<P extends ApacheConfig> implements ApacheConfig {
    private final ImmutableHierarchicalConfiguration configuration;
//    private final P parent;


    public Apache(final ImmutableHierarchicalConfiguration configuration, P parent) {
        this.configuration = configuration;
//        this.parent = parent;
    }


    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

//    public P getParent() {
//        return parent;
//    }
}
