package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.Objects;
import java.util.stream.Stream;

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

    @Override
    public Stream<Difference<?>> underlyingDiffs(T t1, T t2) {
        //Warning: cyclic dependency (unless equals is overwritten in subclasses)
        return equals(t1, t2) ? null : Stream.of(new Unequal<>(this, t1, t2));
    }

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
