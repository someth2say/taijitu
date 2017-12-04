package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public class Missing<MT> extends Mismatch<MT> {

    public Missing(Equalizer<MT> cause, MT composite) {
        super(cause, composite);
    }

}
