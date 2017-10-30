package org.someth2say.taijitu.config.impl;

public abstract class DelegatingConfigImpl<D,P> {
    private final D delegate;
    private final P parent;

    protected DelegatingConfigImpl(D delegate, P parent) {
        this.delegate = delegate;
        this.parent = parent;
    }

    public D getDelegate() {
        return delegate;
    }

    public P getParent() {
        return parent;
    }
}
