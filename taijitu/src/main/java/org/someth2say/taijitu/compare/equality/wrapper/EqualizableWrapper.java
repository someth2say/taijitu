package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public class EqualizableWrapper<T, EQ extends Equalizer<T>>
        extends AbstractWrapper<T,EQ>
        implements Equalizable {

    public EqualizableWrapper(T wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public boolean equals(Object obj) {
        if (getWrapped() == null) {
            return obj == null;
        }
        if (obj instanceof HashableEqualizableWrapper) {
            @SuppressWarnings("unchecked")
            HashableEqualizableWrapper<T> otherWrapper = (HashableEqualizableWrapper<T>) obj;
            T otherWrapped = otherWrapper.getWrapped();
            return getEquality().equals(getWrapped(), otherWrapped);
        }
        return false;
    }
}
