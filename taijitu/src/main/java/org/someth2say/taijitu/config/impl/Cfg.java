package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.interfaces.ICfg;

public abstract class Cfg<P> implements ICfg {
    private final P delegate;
    private final P parent;

    protected Cfg(P delegate, P parent) {
        this.delegate = delegate;
        this.parent = parent;
    }

    public P getDelegate() {
        return delegate;
    }

    public P getParent() {
        return parent;
    }
}
