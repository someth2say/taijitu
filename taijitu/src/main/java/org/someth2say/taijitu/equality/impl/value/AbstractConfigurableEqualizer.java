package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.Objects;

public abstract class AbstractConfigurableEqualizer<T> implements Equalizer<T> {

    private final Object equalityConfig;

    AbstractConfigurableEqualizer(Object equalityConfig) {
        this.equalityConfig = equalityConfig;
    }

    Object getEqualityConfig() {
        return equalityConfig;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + (getEqualityConfig() != null ? getEqualityConfig().toString() : "") + "]";
    }

/*    @Override
    public Stream<Difference> explain(T t1, T t2) {
        //Warning: cyclic dependency (unless areEquals is overwritten in subclasses)
        return areEquals(t1, t2) ? null : Stream.of(new Unequal<>(this, t1, t2));
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getEqualityConfig());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractConfigurableEqualizer)) return false;
        AbstractConfigurableEqualizer<?> other = (AbstractConfigurableEqualizer<?>) obj;

        return Objects.equals(getClass(), obj.getClass()) &&
                Objects.equals(getEqualityConfig(), other.getEqualityConfig());
    }
}
