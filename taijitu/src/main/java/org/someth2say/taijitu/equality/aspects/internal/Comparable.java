package org.someth2say.taijitu.equality.aspects.internal;

/**
 * Internal equality aspect for objects that internally define total ordering.
 * {@link Comparable} instance should follow the same contract than {@link org.someth2say.taijitu.equality.aspects.external.Comparator}.
 * That is, if `comparable_a.equals(comparable_b)==true` then `comparable_a.compareTo(comparable_b)==0` should also be true.
 *
 * @param <T>
 */

public interface Comparable<T> extends Equalizable<T>, java.lang.Comparable<T> {
}
