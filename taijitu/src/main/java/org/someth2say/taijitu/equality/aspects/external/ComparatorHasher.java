package org.someth2say.taijitu.equality.aspects.external;

/**
 * A aspect interface that unites both {@link Comparator} and {@link Hasher} aspects
 *
 * The contract for this interface mixes both extended aspects:
 * <ul>
 *     <li>
 *         {@link ComparatorHasher} follows both {@link Comparator} and {@link Hasher} contract
 *     </li>
 *     <li>
 *         {@link #compare(Object, Object)} and {@link #hash(Object)} method return values need not to be related.
 *         Two object with same hash values can result in any comparison result.
 *         Two objects with same comparison (that is, `comparatorHasher.compareTo(a,b)==0`) may or may not have the same hash code.
 *     </li>
 * </ul>
 *
 */
public interface ComparatorHasher<T> extends Comparator<T>, Hasher<T> {

}
