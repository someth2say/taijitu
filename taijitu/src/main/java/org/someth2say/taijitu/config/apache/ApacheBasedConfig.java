package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

public abstract class ApacheBasedConfig {
    private final ImmutableHierarchicalConfiguration configuration;
//    private final ApacheBasedConfig parent;


    public ApacheBasedConfig(final ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
//        this.parent = parent;
    }


    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

//    public ApacheBasedConfig getParent() {
//        return parent;
//    }
}
