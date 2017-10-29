package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.PluginConfig;

public class ApachePluginConfigNode extends ApacheNamedConfigNode implements PluginConfig {
    protected ApachePluginConfigNode(ImmutableHierarchicalConfiguration configuration) {
        super(configuration,null);
    }
}
