package org.someth2say.taijitu.equality.explain;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

/**
 * Comparison explain class, showing compared elements are not equal, as (at least) one of them is missing.
 * This explain makes sense when comparing collections or instance containers.
 * @param TYPE Type of the underling element that is missing
 */
public class Missing<TYPE> extends Difference<TYPE> {

    public Missing(Equalizer<TYPE> cause, TYPE missing) {
        super(cause, missing);
    }

}
