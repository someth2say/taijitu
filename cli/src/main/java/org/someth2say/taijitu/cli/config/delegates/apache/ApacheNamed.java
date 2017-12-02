package org.someth2say.taijitu.cli.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.delegates.apache.defaults.ApacheCfgDefaults;
import org.someth2say.taijitu.cli.config.delegates.apache.defaults.ApacheNamedCfgDefaults;

abstract class ApacheNamed<P extends ApacheCfgDefaults> extends Apache implements ApacheNamedCfgDefaults {

    ApacheNamed(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }
}