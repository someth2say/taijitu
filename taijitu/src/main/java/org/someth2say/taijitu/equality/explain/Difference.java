package org.someth2say.taijitu.equality.explain;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final Stream<Difference> underlyingDifferences;


    public Difference(Equalizer<? super T> cause, List<T> entries, Stream<Difference> underlyingDifferences) {
        this.cause = cause;
        this.entries = entries;
        this.underlyingDifferences = underlyingDifferences;
    }

    public Difference(Equalizer<? super T> cause, T composite, Stream<Difference> underlyingDifferences) {
        this(cause, Collections.singletonList(composite), underlyingDifferences);
    }

    public Difference(Equalizer<? super T> cause, T composite) {
        this(cause, composite, Stream.empty());
    }

    public Difference(Equalizer<? super T> cause, T composite, T composite2) {
        this(cause, composite, composite2, Stream.empty());
    }

    public Difference(Equalizer<? super T> cause, T composite, T composite2, Stream<Difference> underlyingDifferences) {
        this(cause, Arrays.asList(composite, composite2), underlyingDifferences);
    }


    @Override
    public String toString() {
        if (underlyingDifferences == null) {
            return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(entries, "<>") + ")";
        } else {
            List<Difference> differences = underlyingDifferences.collect(Collectors.toList());
            if (differences.isEmpty()) {
                return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(entries, "<>") + ")";
            } else {
                return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(differences, ",") + ")";
            }
        }
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
