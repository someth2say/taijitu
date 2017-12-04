package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public class EqualizableWrapper<T, EQ extends Equalizer<T>>
        extends AbstractWrapper<T, EQ>
        implements Equalizable {

    public EqualizableWrapper(T wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public boolean equals(Object obj) {
        if (unwrap() == null) {
            return obj == null;
        }
        if (obj instanceof EqualizableWrapper) {
            @SuppressWarnings("unchecked")
            EqualizableWrapper<T, ?> otherWrapper = (EqualizableWrapper<T, ?>) obj;
            T otherWrapped = otherWrapper.unwrap();
            return getEquality().equals(unwrap(), otherWrapped);
        }
        return false;
    }
}