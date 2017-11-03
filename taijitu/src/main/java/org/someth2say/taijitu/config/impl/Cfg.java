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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cfg)) return false;

        Cfg<?> other = (Cfg<?>) o;

        if (getDelegate() != null ? !getDelegate().equals(other.getDelegate()) : other.getDelegate() != null) return false;
        return getParent() != null ? getParent().equals(other.getParent()) : other.getParent() == null;
    }

    @Override
    public int hashCode() {
        int result = getDelegate() != null ? getDelegate().hashCode() : 0;
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        return result;
    }
}
