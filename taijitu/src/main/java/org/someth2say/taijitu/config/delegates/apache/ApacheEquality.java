package org.someth2say.taijitu.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheEqualityCfgDefaults;

public class ApacheEquality extends ApacheNamed<ApacheEqualityCfgDefaults> implements ApacheEqualityCfgDefaults {

    public ApacheEquality(ImmutableHierarchicalConfiguration configuration, ApacheEqualityCfgDefaults parent) {
        super(configuration, parent);
    }

}
