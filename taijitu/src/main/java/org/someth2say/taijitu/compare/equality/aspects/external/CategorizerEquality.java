package org.someth2say.taijitu.compare.equality.external;

import org.someth2say.taijitu.compare.equality.internal.CategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;

public interface CategorizerEquality<T> extends Categorizer<T>, Equality<T> {

    @Override
	default CategorizableEqualizable<T, ? extends CategorizerEquality<T>> wrap(T obj) {
        return new CategorizerEqualityWrapper<>(obj, this);
    }

}
