package org.someth2say.taijitu.equality.aspects.internal;

/**
 * Core aspect for types/instances that contain internal equality definition.
 * Same contract and restrictions than in {@link Object#equals(Object)} apply.
 *
 * In Java, internal equality contract have been always defined by the {@link Object#equals(Object)} method.
 * By the time it was defined, Java did not include generics (and thus, self-types), so the {@link Object#equals(Object)} method
 * only could be created to accept the most generic type: {@link Object}.
 * Nowadays, generics and self-types provides with better expressiveness, but backwards compatibility avoid changing how equality works.
 *
 *
 * @param <T>
 */
public interface Equalizable<T> {
    /**
     * This method is kept for backwards compatibility with classical Object.equals definition.
     *
     * Default implementation should delegate to `equalsTo` method after type-checking the object parameter.
     * Unluckily, default interface methods can not override {@link Object} methods, so overriding should occur in implementing classes.
     *
     * @param obj
     * @return
     */
    @Override
	boolean equals(Object obj);

    /**
     * Method defining the actual internal equality semantics.
     *
     * @param obj
     * @return
     */
    boolean equalsTo(T obj);

}
