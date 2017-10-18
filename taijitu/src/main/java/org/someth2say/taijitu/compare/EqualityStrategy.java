package org.someth2say.taijitu.compare;

/**
 * Based on org.eclipse.collections.api.block.HashingStrategy<E>
 */

public interface EqualityStrategy<E> {

    int computeHashCode(E object);

    boolean equals(E object1, E o2);

    int compare(E object1, E o2);

}
