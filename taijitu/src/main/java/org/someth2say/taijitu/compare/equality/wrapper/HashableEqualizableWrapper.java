package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.HashableEqualizable;

public class HashableEqualizableWrapper<T>
        extends EqualizableWrapper<T, HasherEqualizer<T>>
        implements HashableEqualizable {

    public HashableEqualizableWrapper(T wrapped, HasherEqualizer<T> categorizer) {
        super(wrapped, categorizer);
    }

    // Unluckily, this can not be pushed up to HashableEqualizable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(getWrapped());
    }

}
