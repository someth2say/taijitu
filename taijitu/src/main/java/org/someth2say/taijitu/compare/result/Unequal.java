package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.stream.Stream;

public class Unequal<DT> extends Difference<DT> {

    public Unequal(Equalizer<? super DT> cause, DT composite, DT composite2, Stream<Difference<?>> underlyingDifferences) {
        super(cause, composite, composite2, underlyingDifferences);
    }

    public Unequal(Equalizer<? super DT> cause, DT composite, DT composite2) {
        super(cause, composite, composite2);
    }
}
