package org.someth2say.taijitu.ui.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.ui.config.delegates.apache.defaults.ApacheTaijituCfgDefaults;

public class ApacheTaijitu extends ApacheComparison implements ApacheTaijituCfgDefaults {

    public ApacheTaijitu(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

}
