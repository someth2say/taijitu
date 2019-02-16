package org.someth2say.taijitu.equality.aspects.external;

/**
 * A `hasher` is an object able to compute the hashcode of instances of a defined class.
 * Like {@link Comparator}, {@link Hasher} aspect includes {@link Equalizer} aspect.
 * {@link Hasher} implementations should follow contract:
 * <ul>
 *     <li>Two object with same hashcode should be considered equals by the hasher.
 *     That is, if 'hasher.areEquals(a,b)==true' then 'hasher.hash(a)==hasher.hash(b)'.
 *     Note that two objects having the same hashcode are not needed to be equals.</li> *
 * </ul>
 *
 * @see Comparator
 * @param <T>
 */
public interface Hasher<T> extends Equalizer<T> {
    /**
     * @return the hash code for the input parameter, following the contract.
     */
    int hash(T t);

}
