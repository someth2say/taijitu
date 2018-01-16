package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public abstract class Wrapper<WRAPPED, EQ extends Equalizer<WRAPPED>> {
    private final WRAPPED wrapped;
    private final EQ equality;

    Wrapper(WRAPPED wrapped, EQ equality) {
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
