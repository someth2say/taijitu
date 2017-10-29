package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.node.NamedNode;

public abstract class ApacheNamedConfigNode extends ApacheBasedConfigNode implements NamedNode {
    ApacheNamedConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheBasedConfigNode parent) {
        super(configuration,parent);
    }

    @Override
    public String getName() {
        return getConfiguration().getRootElementName();
    }
}
