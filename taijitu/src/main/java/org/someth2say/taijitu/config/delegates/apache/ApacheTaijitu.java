package org.someth2say.taijitu.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheTaijituCfgDefaults;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

public class ApacheTaijitu extends ApacheComparison implements ApacheTaijituCfgDefaults {

    public ApacheTaijitu(ImmutableHierarchicalConfiguration configuration) {
        super(configuration, null);
    }

    public static ApacheTaijitu fromConfig(ITaijituCfg other){
        //TODO
        return null;
    }
}
