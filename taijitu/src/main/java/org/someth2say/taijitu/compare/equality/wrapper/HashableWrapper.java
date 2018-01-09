package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class HashableWrapper<WRAPPED>
        extends EqualizableWrapper<WRAPPED, Hasher<WRAPPED>>
        implements Hashable<IWraper<WRAPPED,?>>, IWraper<WRAPPED,Hasher<WRAPPED>> {

    public HashableWrapper(WRAPPED wrapped, Hasher<WRAPPED> categorizer) {
        super(wrapped, categorizer);
    }

    @Override
    public int hashCode() {
        return getEquality().hash(getWraped());
    }

}
