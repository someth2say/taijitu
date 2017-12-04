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
        return getEquality().compare(unwrap(), other);
    }

}
