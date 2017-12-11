package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public interface IHashableWraper<T, EQ> extends IEqualizableWraper<T,EQ>, Hashable<IWraper<T, ?>> {
}
