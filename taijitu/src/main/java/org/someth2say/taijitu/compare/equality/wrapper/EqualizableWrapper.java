package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public class EqualizableWrapper<WRAPPED, EQ extends Equalizer<WRAPPED>>
        extends AbstractWrapper<WRAPPED, EQ>
        implements IEqualizableWraper<WRAPPED, EQ> {

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

}
