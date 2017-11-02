package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.interfaces.ICfg;
import org.someth2say.taijitu.util.Named;

public interface INamedCfgDefaults<P extends Named & ICfg> extends Named, ICfgDefaults<P> {

    @Override
    default String getName() {
        return getDelegate().getName();
    }

}
