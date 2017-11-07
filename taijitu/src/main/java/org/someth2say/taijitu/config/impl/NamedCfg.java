package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.impl.defaults.INamedCfgDefaults;
import org.someth2say.taijitu.config.interfaces.ICfg;
import org.someth2say.taijitu.util.Named;

public abstract class NamedCfg<P extends Named & ICfg> extends Cfg<P> implements INamedCfgDefaults<P> {
    protected NamedCfg(P delegate, P parent) {
        super(delegate, parent);
    }
}