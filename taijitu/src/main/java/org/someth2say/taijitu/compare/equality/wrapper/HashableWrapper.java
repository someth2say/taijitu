package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

public class HashableWrapper<T>
        extends EqualizableWrapper<T, Hasher<T>>
        implements IHashableWraper<T, Hasher<T>> {

    public HashableWrapper(T wrapped, Hasher<T> categorizer) {
        super(wrapped, categorizer);
    }

    @Override
    public int hashCode() {
        return getEquality().hashCode(getWraped());
    }

}
