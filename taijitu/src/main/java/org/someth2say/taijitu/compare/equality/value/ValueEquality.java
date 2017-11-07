package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.util.Named;

/**
 * Based on org.eclipse.collections.api.block.HashingStrategy<E>
 * Eclipse Collections use the so called "HashingStrategy", that covers equals/hashcode contract
 * But I do not like this name, as implies "hashing".
 * I do prefer the term "SortedValueEquality", adding the definition for the natural order (compare)
 */
public interface ValueEquality<T> extends Named {

    int computeHashCode(T keyValue);

    boolean equals(T object1, T other);
}
