package org.someth2say.taijitu.ui.config.impl;

import org.someth2say.taijitu.ui.config.impl.defaults.IEqualityCfgDefaults;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;

public class EqualityCfg
        extends NamedCfg<IEqualityCfg>
        implements IEqualityCfgDefaults<IEqualityCfg> {

    public EqualityCfg(IEqualityCfg delegate, IEqualityCfgDefaults parent) {
        super(delegate, parent);
    }


}
