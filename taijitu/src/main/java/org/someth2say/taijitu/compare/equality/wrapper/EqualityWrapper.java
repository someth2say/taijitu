package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.internal.Equalizable;

public class EqualityWrapper<T, EQ extends Equality<T>>
        extends AbstractEqualityWrapper<T,EQ>
        implements Equalizable<T,EQ> {

    public EqualityWrapper(T wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public boolean equals(Object obj) {
        if (getWrapped() == null) {
            return obj == null;
        }
        if (obj instanceof CategorizerEqualityWrapper) {
            @SuppressWarnings("unchecked")
            CategorizerEqualityWrapper<T> otherWrapper = (CategorizerEqualityWrapper<T>) obj;
            T otherWrapped = otherWrapper.getWrapped();
            return getEquality().equals(getWrapped(), otherWrapped);
        }
        return false;
    }
}
