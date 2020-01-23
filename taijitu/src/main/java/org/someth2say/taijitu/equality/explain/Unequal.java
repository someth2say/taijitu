package org.someth2say.taijitu.equality.explain;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

/**
 * Comparison explain class, showing two instances contains different values or meanings.
 * @param TYPE The more specific common type for both elements that are declared not equals.
 */
public class Unequal<TYPE> extends Difference<TYPE> {

    public Unequal(Equalizer<TYPE> cause, TYPE lhs, TYPE rhs) {
        super(cause, lhs, rhs);
    }
}
