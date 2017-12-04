package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableHashableEqualizable;

public class ComparableHashableEqualizableWrapper<T, EQ extends ComparatorHasherEqualizer<T>>
        extends EqualizableWrapper<T, EQ>
        implements ComparableHashableEqualizable<T> {

    public ComparableHashableEqualizableWrapper(T wrapped, EQ comparer) {
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
        if (obj instanceof HashableEqualizableWrapper) {
            @SuppressWarnings("unchecked")
            HashableEqualizableWrapper<T> otherWrapper = (HashableEqualizableWrapper<T>) obj;
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
