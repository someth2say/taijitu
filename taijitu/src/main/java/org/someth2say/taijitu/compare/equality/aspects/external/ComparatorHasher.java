package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableHashable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableWrapper;

public interface ComparatorHasher<T> extends Comparator<T>, Hasher<T> {

    @Override
    default ComparableHashable<T> wrap(T obj) {
        return new ComparableHashableWrapper<>(obj, this);
    }

}
