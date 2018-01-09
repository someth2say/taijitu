package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public abstract class AbstractWrapper<WRAPPED, EQ extends Equalizer<WRAPPED>>  implements IWraper<WRAPPED,EQ>{
    private final WRAPPED wrapped;
    private final EQ equality;

    AbstractWrapper(WRAPPED wrapped, EQ equality) {
        this.equality = equality;
        this.wrapped = wrapped;
    }

	public WRAPPED getWraped() {
        return wrapped;
    }

	public EQ getEquality() {
        return equality;
    }
}
