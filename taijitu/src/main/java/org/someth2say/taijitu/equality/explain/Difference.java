package org.someth2say.taijitu.equality.explain;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Abstract explanation class abstracting over the underlying difference between two instances (under given equality definition).
 * Differences can be of many kind, i.e. having different values, different inner order, missing values, depending on the 
 * equality used.
 *
 * @see Missing
 * @see Unequal
 */
public abstract class Difference<TYPE> {
    private final Equalizer<? super TYPE> cause;
    private final Collection<TYPE> entries;

    public Difference(Equalizer<? super TYPE> cause, Collection<TYPE> elements) {
        this.cause = cause;
        this.entries = elements;
    }

    public Difference(Equalizer<? super TYPE> cause, TYPE element) {
        this(cause, Collections.singletonList(element));
    }

    public Difference(Equalizer<? super TYPE> cause, TYPE lhs, TYPE rhs) {
        this(cause, Arrays.asList(lhs, rhs));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(entries, "<>") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Difference)) return false;
        Difference<?> difference = (Difference<?>) o;
        return Objects.equals(cause, difference.cause) &&
                Objects.equals(entries, difference.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, cause);}

    public Collection<TYPE> getEntries() {
        return entries;
    }

}
