package org.someth2say.taijitu.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheSourceCfgDefaults;

public class ApacheSource extends ApacheNamed<ApacheSourceCfgDefaults> implements ApacheSourceCfgDefaults {


    public ApacheSource(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }


}
