package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.List;

public class Missing<MT> extends Mismatch<MT> {
    public Missing(Equality<MT> cause, List<MT> entries, List<Mismatch> underlyingMismatches) {
        super(cause, entries, underlyingMismatches);
    }

    public Missing(Equality<MT> cause, List<MT> entries) {
        super(cause, entries);
    }

    public Missing(Equality<MT> cause, MT composite, List<Mismatch> underlyingMismatches) {
        super(cause, composite, underlyingMismatches);
    }

    public Missing(Equality<MT> cause, MT composite) {
        super(cause, composite);
    }

    public Missing(Equality<MT> cause, MT composite, MT composite2) {
        super(cause, composite, composite2);
    }

    public Missing(Equality<MT> cause, MT composite, MT composite2, List<Mismatch> underlyingMismatches) {
        super(cause, composite, composite2, underlyingMismatches);
    }
}
