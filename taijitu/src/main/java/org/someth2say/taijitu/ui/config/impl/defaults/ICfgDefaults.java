package org.someth2say.taijitu.ui.config.impl.defaults;

import org.someth2say.taijitu.ui.config.interfaces.ICfg;

interface ICfgDefaults<T extends ICfg> {
    T getDelegate();

    T getParent();
}
