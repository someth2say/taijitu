package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

public abstract class ApacheBasedConfigNode {
    private final ImmutableHierarchicalConfiguration configuration;

    public ApacheBasedConfigNode(final ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }
}
