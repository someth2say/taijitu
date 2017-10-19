package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.util.Named;

/**
 * Based on org.eclipse.collections.api.block.HashingStrategy<E>
 * Eclipse Collections use the so called "HashingStrategy", that covers equals/hashcode contract
 * But I do not like this name, as implies "hashing".
 * I do prefer the term "EqualityStrategy", adding the definition for the natural order (compare)
 */
public interface EqualityStrategy extends Named {

    int computeHashCode(Object keyValue, Object equalityConfig);

    boolean equals(Object object1, Object o2, Object equalityConfig);

    int compare(Object object1, Object o2, Object equalityConfig);

}
