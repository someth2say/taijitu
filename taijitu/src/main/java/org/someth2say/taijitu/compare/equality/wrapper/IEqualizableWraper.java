package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public interface IEqualizableWraper<T, EQ extends Equalizer<T>> extends Equalizable<IWraper<T, ?>>, IWraper<T, EQ> {
}
