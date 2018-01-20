package org.someth2say.taijitu.compare.equality.proxy;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

public class HasherInterceptor<T, EQ extends Hasher<T>> extends EqualizableInterceptor<T, EQ>
        implements IHasherInterceptor<T,EQ>{

    public HasherInterceptor(EQ hasher) {
        super(hasher);
    }
}