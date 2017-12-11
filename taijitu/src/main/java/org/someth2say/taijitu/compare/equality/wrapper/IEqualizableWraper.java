package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;

public interface IEqualizableWraper<T, EQ> extends Equalizable<IWraper<T, ?>>, IWraper<T, EQ> {
}