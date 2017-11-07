package org.someth2say.taijitu.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheCfgDefaults;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheNamedCfgDefaults;

public abstract class ApacheNamed<P extends ApacheCfgDefaults> extends Apache<P> implements ApacheNamedCfgDefaults {

    public ApacheNamed(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }
}