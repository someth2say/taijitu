package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.node.NamedNode;

public class ApacheNamedConfigNode extends ApacheBasedConfigNode implements NamedNode {
    protected ApacheNamedConfigNode(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return getConfiguration().getRootElementName();
    }
}
