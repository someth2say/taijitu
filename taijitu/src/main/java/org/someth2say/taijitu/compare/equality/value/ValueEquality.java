package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.util.Named;

/**
 * Based on org.eclipse.collections.api.block.HashingStrategy<E>
 * Eclipse Collections use the so called "HashingStrategy", that covers equals/hashcode contract
 * But I do not like this name, as implies "hashing".
 * I do prefer the term "ValueEquality", adding the definition for the natural order (compare)
 */
public interface ValueEquality<T> extends Named {

    int computeHashCode(T keyValue, Object equalityConfig);

    boolean equals(T object1, T o2, Object equalityConfig);

    int compare(T object1, T o2, Object equalityConfig);

}
