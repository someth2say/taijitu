package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheSourceConfig;

public class ApacheSource extends ApacheNamed<ApacheSourceConfig> implements ApacheSourceConfig {


    public ApacheSource(ImmutableHierarchicalConfiguration configuration, ApacheSourceConfig parent) {
        super(configuration, parent);
    }


}
