package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.Objects;

//TODO: Remove this. Abstract configuration is a CLI topic, not a comparator topic.
@Deprecated
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
