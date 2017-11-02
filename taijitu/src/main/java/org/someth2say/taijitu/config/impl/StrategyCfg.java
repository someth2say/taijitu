package org.someth2say.taijitu.config.impl;


import org.someth2say.taijitu.config.impl.defaults.IStrategyCfgDefaults;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;

public class StrategyCfg
        extends NamedCfg<IStrategyCfg>
        implements IStrategyCfgDefaults<IStrategyCfg> {

    public StrategyCfg(IStrategyCfg delegate, IStrategyCfg parent) {
        super(delegate, parent);
    }
}
