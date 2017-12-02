package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.config.interfaces.ICfg;

interface ICfgDefaults<T extends ICfg> {
    T getDelegate();

    T getParent();
}
