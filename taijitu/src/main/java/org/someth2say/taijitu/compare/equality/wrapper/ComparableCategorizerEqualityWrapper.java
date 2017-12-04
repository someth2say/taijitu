package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableCategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorCategorizerEquality;

public class ComparableCategorizerEqualityWrapper<T, EQ extends ComparatorCategorizerEquality<T>>
        extends EqualityWrapper<T, EQ>
        implements ComparableCategorizableEqualizable<T, EQ> {

    public ComparableCategorizerEqualityWrapper(T wrapped, EQ comparer) {
        super(wrapped, comparer);
    }

    @Override
    public int compareTo(T other) {
        return getEquality().compare(getWrapped(), other);
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

    @Override
    public int hashCode() {
        return getEquality().hashCode(getWrapped());
    }

}
