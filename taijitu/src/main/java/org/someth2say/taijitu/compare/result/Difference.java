package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.List;

public class Difference<DT> extends Mismatch<DT> {

    public Difference(Equalizer<DT> cause, DT composite, DT composite2, List<Mismatch<?>> underlyingMismatches) {
        super(cause, composite, composite2, underlyingMismatches);
    }

    public Difference(Equalizer<DT> cause, DT composite, DT composite2) {
        super(cause, composite, composite2);
    }
}
