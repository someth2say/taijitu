package org.someth2say.taijitu.compare.result;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract comparison result class expressing some entries are not equal (given the generating equality).
 * Differences can be of many kind, i.e. having different values, different inner order, missing values, depending on the 
 * equality used. 
 */
public abstract class Difference<MMT> {
    private final Equalizer<? super MMT> cause;
    private final List<MMT> entries;
    private final Stream<Difference> underlyingDifferences;


    public Difference(Equalizer<? super MMT> cause, List<MMT> entries, Stream<Difference> underlyingDifferences) {
        this.cause = cause;
        this.entries = entries;
        this.underlyingDifferences = underlyingDifferences;
    }

    public Difference(Equalizer<? super MMT> cause, MMT composite, Stream<Difference> underlyingDifferences) {
        this(cause, Collections.singletonList(composite), underlyingDifferences);
    }

    public Difference(Equalizer<? super MMT> cause, MMT composite) {
        this(cause, composite, Stream.empty());
    }

    public Difference(Equalizer<? super MMT> cause, MMT composite, MMT composite2) {
        this(cause, composite, composite2, Stream.empty());
    }

    public Difference(Equalizer<? super MMT> cause, MMT composite, MMT composite2, Stream<Difference> underlyingDifferences) {
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
}
