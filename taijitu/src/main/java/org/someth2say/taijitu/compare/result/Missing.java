package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

/**
 * Comparison result class, showing compared elements are not equal, as (at least) one of them is missing.
 * This result makes sense when comparing collections or instance containers.
 */
public class Missing<MT> extends Difference<MT> {

    public Missing(Equalizer<MT> cause, MT composite) {
        super(cause, composite);
    }

}
