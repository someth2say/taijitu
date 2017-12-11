package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class HashableWrapper<T>
        extends EqualizableWrapper<T, Hasher<T>>
        implements Hashable<T> {

    public HashableWrapper(T wrapped, Hasher<T> categorizer) {
        super(wrapped, categorizer);
    }

    // Unluckily, this can not be pushed up to Hashable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(unwrap());
    }

}
