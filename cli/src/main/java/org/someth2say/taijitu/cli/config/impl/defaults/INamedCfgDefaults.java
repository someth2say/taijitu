package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.config.interfaces.ICfg;
import org.someth2say.taijitu.cli.util.Named;

public interface INamedCfgDefaults<P extends Named & ICfg> extends Named, ICfgDefaults<P> {

    @Override
    default String getName() {
        return getDelegate().getName();
    }

}
