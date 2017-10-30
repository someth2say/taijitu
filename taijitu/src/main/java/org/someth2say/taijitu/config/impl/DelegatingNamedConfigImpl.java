package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.util.Named;

public abstract class DelegatingNamedConfigImpl<P, D extends Named> extends DelegatingConfigImpl<D> implements Named {
    public DelegatingNamedConfigImpl(D delegate) {
        super(delegate);
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }
}
