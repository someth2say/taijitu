package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableHashableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableEqualizableWrapper;

public interface ComparatorHasherEqualizer<T> extends ComparatorEqualizer<T>, HasherEqualizer<T> {

    @Override
    default ComparableHashableEqualizable<T> wrap(T obj) {
        return new ComparableHashableEqualizableWrapper<>(obj, this);
    }

}
