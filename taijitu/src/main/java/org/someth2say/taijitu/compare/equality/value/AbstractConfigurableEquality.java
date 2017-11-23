package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    @Override
    public List<Mismatch> differences(T t1, T t2) {
        return this.equals(t1, t2) ? Collections.emptyList() : Collections.singletonList(new Difference<>(this, t1, t2));
    }

}
