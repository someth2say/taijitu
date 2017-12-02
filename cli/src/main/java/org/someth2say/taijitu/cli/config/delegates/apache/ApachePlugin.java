package org.someth2say.taijitu.cli.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.delegates.apache.defaults.ApachePluginCfgDefaults;

public class ApachePlugin extends ApacheNamed<ApachePluginCfgDefaults> implements ApachePluginCfgDefaults {

    public ApachePlugin(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }
}
