package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableHashableWrapper;

public interface ComparatorHasher<T> extends Comparator<T>, Hasher<T> {

    @Override
    default IComparableHashableWrapper<T,?> wrap(T obj) {
        return new ComparableHashableWrapper<>(obj, this);
    }

}
