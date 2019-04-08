package org.someth2say.taijitu.equality.explain;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract explanation class abstracting over the underlying difference between two instances (under given equality definition).
 * Differences can be of many kind, i.e. having different values, different inner order, missing values, depending on the 
 * equality used.
 *
 * @see Missing
 * @see Unequal
 */
public abstract class Difference<T> {
    private final Equalizer<? super T> cause;
    private final List<T> entries;

    public Difference(Equalizer<? super T> cause, List<T> entries) {
        this.cause = cause;
        this.entries = entries;
    }

    public Difference(Equalizer<? super T> cause, T composite) {
        this(cause, Collections.singletonList(composite));
    }

    public Difference(Equalizer<? super T> cause, T composite, T composite2) {
        this(cause, Arrays.asList(composite, composite2));
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

    public List<T> getEntries() {
        return entries;
    }

}
