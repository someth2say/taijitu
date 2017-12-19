package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public interface IWraper<T, EQ extends Equalizer> extends IEqualityHolder<EQ> {

    T getWraped();

}
