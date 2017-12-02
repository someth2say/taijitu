package org.someth2say.taijitu.cli.config.impl;

import org.someth2say.taijitu.cli.config.impl.defaults.INamedCfgDefaults;
import org.someth2say.taijitu.cli.config.interfaces.ICfg;
import org.someth2say.taijitu.cli.util.Named;

abstract class NamedCfg<P extends Named & ICfg> extends Cfg<P> implements INamedCfgDefaults<P> {
    NamedCfg(P delegate, P parent) {
        super(delegate, parent);
    }
}
