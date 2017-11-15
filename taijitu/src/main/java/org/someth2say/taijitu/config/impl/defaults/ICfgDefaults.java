package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.interfaces.ICfg;

interface ICfgDefaults<T extends ICfg> {
    T getDelegate();

    T getParent();
}
