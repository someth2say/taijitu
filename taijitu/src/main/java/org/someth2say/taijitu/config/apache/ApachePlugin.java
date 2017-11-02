package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApachePluginConfig;

public class ApachePlugin extends ApacheNamed<ApachePluginConfig> implements ApachePluginConfig {

    public ApachePlugin(ImmutableHierarchicalConfiguration configuration, ApachePluginConfig parent) {
        super(configuration, parent);
    }
}
