package org.someth2say.taijitu.equality.aspects.external;

/**
 * The {@link Comparator} aspect behaves and acts exactly as the {@link java.util.Comparator} interface.
 * It defines a total order for the compared objects.
 *
 * The only difference is that {@link Comparator} aspect is an extension of the {@link Equalizer} aspect.
 * Like in {@link Hasher}, {@link Comparator} contract defines it relation with {@link Equalizer} as follows:
 * <ul>
 *     <li>
 *         The same contract than in {@link java.util.Comparator} applies.
 *     </li>
 *     <li>
 *         In addition, the {@link #compare(Object, Object)} methods implemented should be coherent with the {@link #areEquals(Object, Object)} method.
 *         That is, if `comparator.areEquals(a,b)` then `comparator.compare(a,b)==0`s should be true.
 *     </li>
 * </ul>
 *
 * @see Hasher
 *
 */
public interface Comparator<T> extends java.util.Comparator<T>, Equalizer<T> {

    /**
     * This method is just a convenient shortcut, that allows type-checking the parameters in compile time.
     * By default, this method just delegates to the {@link #compare(Object, Object)} method.
     * The only reason to overwrite this behaviour is to provide a implementation for {@link #compare(Object, Object)} that skips the type-checking.
     * In this case, it is recommended also to overwrite the  {@link #compare(Object, Object)} method to delegate to this method after type-checking parameters.
     * @return The same value than {@link #compare(Object, Object)}
     */
    default int compareTo(T a, T b){
        return this.compare(a,b);
    }

}
