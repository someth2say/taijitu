package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.Equality;

//TODO: For equality purposes, those need not to be Named! Naming should be something external
public abstract class AbstractConfigurableEquality<T> implements Equality<T> {

    private final Object equalityConfig;

    protected AbstractConfigurableEquality(Object equalityConfig) {
        this.equalityConfig = equalityConfig;
    }

    Object getEqualityConfig() {
        return equalityConfig;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + (getEqualityConfig() != null ? getEqualityConfig().toString() : "") + "]";
    }

}
