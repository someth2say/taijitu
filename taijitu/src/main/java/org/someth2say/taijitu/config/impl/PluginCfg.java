package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.impl.defaults.IPluginCfgDefaults;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;

/**
 * Right now, plugins have no configuration, only name references
 */
public class PluginCfg
        extends NamedCfg<IPluginCfg>
        implements IPluginCfgDefaults<IPluginCfg> {

    public PluginCfg(IPluginCfg delegate, IPluginCfgDefaults parent) {
        super(delegate, parent);
    }
}