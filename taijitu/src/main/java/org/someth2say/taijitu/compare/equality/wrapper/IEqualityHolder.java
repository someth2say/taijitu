package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public interface IEqualityHolder<EQ extends Equalizer> {
    EQ getEquality();
}
