package org.someth2say.taijitu.compare.equality.proxy;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public class EqualizableInterceptor<T, EQ extends Equalizer<T>>
        implements IEqualizableInterceptor<T,EQ> {

    private final EQ equalizer;

    public EqualizableInterceptor(EQ equalizer) {
        this.equalizer = equalizer;
    }

    @Override
    public EQ getEquality() {
        return equalizer;
    }


}