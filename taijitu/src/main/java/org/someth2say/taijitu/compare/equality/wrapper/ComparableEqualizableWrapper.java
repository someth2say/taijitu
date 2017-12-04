package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableEqualizable;

public class ComparableEqualizableWrapper<T,EQ extends ComparatorEqualizer<T>>
        extends EqualizableWrapper<T, EQ>
        implements ComparableEqualizable<T> {

    public ComparableEqualizableWrapper(T wrapped, EQ comparer) {
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

}
