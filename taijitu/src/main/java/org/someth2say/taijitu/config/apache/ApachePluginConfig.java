package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegate.PluginConfigDelegate;

public class ApachePluginConfig extends ApacheNamedConfig implements PluginConfigDelegate {
    protected ApachePluginConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }
}
