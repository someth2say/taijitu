package org.someth2say.taijitu.compare.equality.external;

import org.someth2say.taijitu.compare.equality.internal.ComparableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableEqualityWrapper;

import java.util.Comparator;

public interface ComparatorEquality<T> extends Comparator<T>, Equality<T> {

    default ComparableEqualizable<T, ? extends ComparatorEquality<T>> wrap(T obj) {
        return new ComparableEqualityWrapper<>(obj, this);
    }

}
