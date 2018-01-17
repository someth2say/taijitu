package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public class EqualizableWrapper<WRAPPED>
        extends Wrapper<WRAPPED>
        implements Equalizable<Wrapper<WRAPPED>> {

    public Equalizer<WRAPPED> getEqualizer() {
        return equality;
    }

    private final Equalizer<WRAPPED> equality;

    public EqualizableWrapper(WRAPPED wrapped, Equalizer<WRAPPED> equality) {
        super(wrapped);
        this.equality = equality;
    }

    @Override
    public boolean equals(Object obj) {
        if (getWraped() == null) {
            return obj == null;
        }
        if (obj instanceof EqualizableWrapper) {
            @SuppressWarnings("unchecked")
            EqualizableWrapper<WRAPPED> otherWrapper = (EqualizableWrapper<WRAPPED>) obj;
            return equalsTo(otherWrapper);
        }
        return false;
    }

    @Override
    public boolean equalsTo(Wrapper<WRAPPED> other) {
        return getEqualizer().areEquals(getWraped(), other.getWraped());
    }

    public static class Factory<FWRAPPED> implements Wrapper.Factory<FWRAPPED> {
        private final Equalizer<FWRAPPED> equalizer;

        public Factory(Equalizer<FWRAPPED> equalizer) {
            this.equalizer = equalizer;
        }

        @Override
        public EqualizableWrapper<FWRAPPED> wrap(FWRAPPED wrapped) {
            return new EqualizableWrapper<>(wrapped, equalizer);
        }
    }
}

