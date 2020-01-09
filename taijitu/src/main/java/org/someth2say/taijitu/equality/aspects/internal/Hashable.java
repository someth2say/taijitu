package org.someth2say.taijitu.equality.aspects.internal;

/**
 * Internal equality aspect for objects that define hashcode internally.
 * {@link Hashable} instance should follow the same contract than {@link org.someth2say.taijitu.equality.aspects.external.Hasher}.
 * That is, if `hashable_a.equals(hashable_b)==true` then `hashable_a.hash()==hashable_b.hash()` should also be true.
 *
 * @param <T>
 */
public interface Hashable<T> extends Equalizable<T> {

    @Override
    int hashCode();
}
