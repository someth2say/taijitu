package org.someth2say.taijitu.compare.equality.value;

public abstract class AbstractValueEquality<T> implements ValueEquality<T> {
    private final Object equalityConfig;

    public AbstractValueEquality(Object equalityConfig) {
        this.equalityConfig = equalityConfig;
    }

    Object getEqualityConfig() {
        return equalityConfig;
    }

    @Override
    public String toString() {
        return getName() + "[" + (getEqualityConfig() != null ? getEqualityConfig().toString() : "") + "]";
    }
}
