package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.CategorizableEqualizable;

public class CategorizerEqualityWrapper<T>
        extends EqualityWrapper<T, HasherEqualizer<T>>
        implements CategorizableEqualizable<T> {

    public CategorizerEqualityWrapper(T wrapped, HasherEqualizer<T> categorizer) {
        super(wrapped, categorizer);
    }

    // Unluckily, this can not be pushed up to CategorizableEqualizable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(getWrapped());
    }

}
