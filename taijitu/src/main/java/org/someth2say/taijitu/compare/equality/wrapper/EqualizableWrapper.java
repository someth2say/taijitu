package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public class EqualizableWrapper<T, EQ extends Equalizer<T>>
        extends AbstractWrapper<T, EQ>
        implements IEqualizableWraper<T, EQ> {

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
            return equalsTo(otherWrapper);
        }
        return false;
    }

    @Override
    public boolean equalsTo(IWraper<T, ?> other) {
        return getEquality().equals(unwrap(), other.unwrap());
    }

}
