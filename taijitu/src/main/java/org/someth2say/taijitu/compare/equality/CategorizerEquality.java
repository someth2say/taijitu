package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;

public interface CategorizerEquality<T> extends Categorizer<T>, Equality<T> {

    default CategorizerEqualityWrapper<T> wrap(T obj) {
        return new CategorizerEqualityWrapper<>(obj,this);
    }

}
