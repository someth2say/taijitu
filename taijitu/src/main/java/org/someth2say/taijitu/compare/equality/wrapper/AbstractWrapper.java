package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public abstract class AbstractWrapper<T, EQ extends Equalizer<T>>  implements IWraper<T,EQ>{
    private final T wrapped;
    private final EQ equality;

    AbstractWrapper(T wrapped, EQ equality) {
        this.equality = equality;
        this.wrapped = wrapped;
    }

	public T getWraped() {
        return wrapped;
    }

	public EQ getEquality() {
        return equality;
    }
}
