package org.someth2say.taijitu.compare;

/**
 * Based on org.eclipse.collections.api.block.HashingStrategy<E>
 * Eclipse Collections use the so called "HashingStrategy", that covers equals/hashcode contract
 * But I do not like this name, as implies "hashing".
 * I do prefer the term "EqualityStrategy", adding the definition for the natural order (compare)
 */
public interface EqualityStrategy<E> {

    int computeHashCode(E object);

    boolean equals(E object1, E o2);

    int compare(E object1, E o2);

}
