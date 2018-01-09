package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public class EqualizableWrapper<WRAPPED, EQ extends Equalizer<WRAPPED>>
        extends AbstractWrapper<WRAPPED, EQ>
        implements Equalizable<IWraper<WRAPPED,?>>,IWraper<WRAPPED,EQ> {

    public EqualizableWrapper(WRAPPED wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public boolean equals(Object obj) {
        if (getWraped() == null) {
            return obj == null;
        }
        if (obj instanceof EqualizableWrapper) {
            @SuppressWarnings("unchecked")
            EqualizableWrapper<WRAPPED, ?> otherWrapper = (EqualizableWrapper<WRAPPED, ?>) obj;
            return equalsTo(otherWrapper);
        }
        return false;
    }

    @Override
    public boolean equalsTo(IWraper<WRAPPED, ?> other) {
        return getEquality().areEquals(getWraped(), other.getWraped());
    }

    public class Factory<FWRAPPED> {
        private final Equalizer<FWRAPPED> equalizer;

        public Factory(Equalizer<FWRAPPED> equalizer){
            this.equalizer = equalizer;
        }

        public EqualizableWrapper<FWRAPPED, Equalizer<FWRAPPED>> wrapp(FWRAPPED wrapped){
            return new EqualizableWrapper<>(wrapped, equalizer);
        }
    }
}
