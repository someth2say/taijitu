package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

public abstract class ApacheBasedConfigNode {
    private final ImmutableHierarchicalConfiguration configuration;
    private final ApacheBasedConfigNode parent;


    public ApacheBasedConfigNode(final ImmutableHierarchicalConfiguration configuration) {
        this(configuration,null);
    }

    public ApacheBasedConfigNode(final ImmutableHierarchicalConfiguration configuration, ApacheBasedConfigNode parent) {
        this.configuration = configuration;
        this.parent = parent;
    }


    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public ApacheBasedConfigNode getParent() {
        return parent;
    }
}
