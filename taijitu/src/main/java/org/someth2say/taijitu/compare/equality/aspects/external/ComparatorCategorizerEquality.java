package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableCategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableCategorizerEqualityWrapper;

public interface ComparatorCategorizerEquality<T> extends ComparatorEquality<T>, CategorizerEquality<T> {

    @Override
    default ComparableCategorizableEqualizable<T, ? extends ComparatorCategorizerEquality<T>> wrap(T obj) {
        return new ComparableCategorizerEqualityWrapper<>(obj, this);
    }

}
