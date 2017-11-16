package org.someth2say.taijitu.ui.config.impl;

import org.someth2say.taijitu.ui.config.interfaces.ICfg;

import java.util.Objects;

abstract class Cfg<P> implements ICfg {
    private final P delegate;
    private final P parent;

    Cfg(P delegate, P parent) {
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

        return Objects.equals(getDelegate(), other.getDelegate()) &&
                Objects.equals(getParent(), other.getParent());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getDelegate(), getParent());
    }
}
