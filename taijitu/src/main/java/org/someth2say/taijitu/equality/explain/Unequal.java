package org.someth2say.taijitu.equality.explain;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.stream.Stream;

/**
 * Comparison explain class, showing two instances contains different values or meanings.
 * 
 */
public class Unequal<T> extends Difference<T> {

    public Unequal(Equalizer<T> cause, T composite, T composite2, Stream<Difference> underlyingDifferences) {
        super(cause, composite, composite2, underlyingDifferences);
    }

    public Unequal(Equalizer<T> cause, T composite, T composite2) {
        super(cause, composite, composite2);
    }
}
