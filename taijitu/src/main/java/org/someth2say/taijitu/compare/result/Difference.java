package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.List;

public class Difference<DT> extends Mismatch<DT> {
    public Difference(Equality<DT> cause, List<DT> entries) {
        super(cause, entries);
    }

    public Difference(Equality<DT> cause, DT composite, List<Mismatch<?>> underlyingMismatches) {
        super(cause, composite, underlyingMismatches);
    }

    public Difference(Equality<DT> cause, DT composite, DT composite2, List<Mismatch<?>> underlyingMismatches) {
        super(cause, composite, composite2, underlyingMismatches);
    }

    public Difference(Equality<DT> cause, List<DT> entries, List<Mismatch<?>> underlyingMismatches) {
        super(cause, entries, underlyingMismatches);
    }

    public Difference(Equality<DT> cause, DT composite, DT composite2) {
        super(cause, composite, composite2);
    }
}
