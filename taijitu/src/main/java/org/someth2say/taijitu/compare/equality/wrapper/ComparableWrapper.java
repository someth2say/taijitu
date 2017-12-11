package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;

public class ComparableWrapper<T,EQ extends Comparator<T>>
        extends EqualizableWrapper<T, EQ>
        implements Comparable<T> {

    public ComparableWrapper(T wrapped, EQ comparer) {
        super(wrapped, comparer);
    }

    @Override
    public int compareTo(T other) {
        return getEquality().compare(unwrap(), other);
    }

}
