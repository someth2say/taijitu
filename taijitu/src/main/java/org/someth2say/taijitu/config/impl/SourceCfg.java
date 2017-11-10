package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.impl.defaults.ISourceCfgDefaults;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;

public class SourceCfg
        extends NamedCfg<ISourceCfg>
        implements ISourceCfgDefaults<ISourceCfg> {

    public SourceCfg(ISourceCfg delegate, ISourceCfgDefaults parent) {
        super(delegate, parent);
    }

}
