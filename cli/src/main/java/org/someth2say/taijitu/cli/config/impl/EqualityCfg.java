package org.someth2say.taijitu.cli.config.impl;

import org.someth2say.taijitu.cli.config.impl.defaults.IEqualityCfgDefaults;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;

public class EqualityCfg
        extends NamedCfg<IEqualityCfg>
        implements IEqualityCfgDefaults<IEqualityCfg> {

    public EqualityCfg(IEqualityCfg delegate, IEqualityCfgDefaults parent) {
        super(delegate, parent);
    }


}
