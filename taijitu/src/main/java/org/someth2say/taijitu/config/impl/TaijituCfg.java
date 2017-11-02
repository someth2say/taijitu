package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.impl.defaults.ITaijituCfgDefaults;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

public class TaijituCfg
        extends Cfg<ITaijituCfg>
        implements ITaijituCfgDefaults<ITaijituCfg> {

    public TaijituCfg(ITaijituCfg delegate) {
        super(delegate, null);
    }


}
