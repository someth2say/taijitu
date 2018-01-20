package org.someth2say.taijitu.compare.equality.proxy;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;

public class ComparatorInterceptor<T, EQ extends Comparator<T>> extends EqualizableInterceptor<T, EQ>
        implements IComparatorInterceptor<T,EQ> {

    public ComparatorInterceptor(EQ hasher) {
        super(hasher);
    }

}