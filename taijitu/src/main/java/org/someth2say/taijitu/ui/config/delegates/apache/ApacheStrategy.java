package org.someth2say.taijitu.ui.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.ui.config.delegates.apache.defaults.ApacheStrategyCfgDefaults;


public class ApacheStrategy extends ApacheNamed<ApacheStrategyCfgDefaults> implements ApacheStrategyCfgDefaults {

    public ApacheStrategy(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }
}
