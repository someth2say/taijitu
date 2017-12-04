package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableCategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableCategorizerEqualityWrapper;

public interface ComparatorHasherEqualizer<T> extends ComparatorEqualizer<T>, HasherEqualizer<T> {

    @Override
    default ComparableCategorizableEqualizable<T> wrap(T obj) {
        return new ComparableCategorizerEqualityWrapper<>(obj, this);
    }

}
