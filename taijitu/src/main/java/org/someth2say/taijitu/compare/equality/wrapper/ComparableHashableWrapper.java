package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableHashable;

public class ComparableHashableWrapper<T, EQ extends ComparatorHasher<T>>
        extends EqualizableWrapper<T, EQ>
        implements ComparableHashable<T> {

    public ComparableHashableWrapper(T wrapped, EQ comparer) {
        super(wrapped, comparer);
    }

    @Override
    public int compareTo(T other) {
        return getEquality().compare(unwrap(), other);
    }

    // Unluckily, this can not be pushed up to Hashable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(unwrap());
    }

}
