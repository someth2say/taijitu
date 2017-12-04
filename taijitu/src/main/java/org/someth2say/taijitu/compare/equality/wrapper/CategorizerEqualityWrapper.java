package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.internal.CategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.aspects.external.CategorizerEquality;

public class CategorizerEqualityWrapper<T>
        extends EqualityWrapper<T, CategorizerEquality<T>>
        implements CategorizableEqualizable<T, CategorizerEquality<T>> {

    public CategorizerEqualityWrapper(T wrapped, CategorizerEquality<T> categorizer) {
        super(wrapped, categorizer);
    }

    // Unluckily, this can not be pushed up to CategorizableEqualizable interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(getWrapped());
    }

}
