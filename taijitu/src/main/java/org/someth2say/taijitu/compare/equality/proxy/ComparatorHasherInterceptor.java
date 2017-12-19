package org.someth2say.taijitu.compare.equality.proxy;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;

public class ComparatorHasherInterceptor<T, EQ extends ComparatorHasher<T>> extends EqualizableInterceptor<T, EQ> implements IComparatorInterceptor<T,EQ>, IHasherInterceptor<T,EQ>{

    public ComparatorHasherInterceptor(EQ comparatorHasher) {
        super(comparatorHasher);
    }

}