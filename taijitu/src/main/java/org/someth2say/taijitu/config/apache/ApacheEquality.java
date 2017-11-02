package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheEqualityConfig;

public class ApacheEquality extends ApacheNamed<ApacheEqualityConfig> implements ApacheEqualityConfig {

    public ApacheEquality(ImmutableHierarchicalConfiguration configuration, ApacheEqualityConfig parent) {
        super(configuration, parent);
    }

}
