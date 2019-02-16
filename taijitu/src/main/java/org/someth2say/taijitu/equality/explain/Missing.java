package org.someth2say.taijitu.equality.explain;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

/**
 * Comparison explain class, showing compared elements are not equal, as (at least) one of them is missing.
 * This explain makes sense when comparing collections or instance containers.
 */
public class Missing<MT> extends Difference<MT> {

    public Missing(Equalizer<MT> cause, MT composite) {
        super(cause, composite);
    }

}
