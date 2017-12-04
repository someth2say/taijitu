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
        return getEquality().compare(unwrap(), other);
    }

    // Unluckily, this can not be pushed up to HashableEqualizable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(unwrap());
    }

}
