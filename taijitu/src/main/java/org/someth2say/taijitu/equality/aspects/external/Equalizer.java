package org.someth2say.taijitu.equality.aspects.external;

import org.someth2say.taijitu.equality.impl.delegating.DelegatingEqualizer;

/**
 * Root aspect for external equality.
 * This interface represents objects able to respond if two instances of the same type are equal.
 * The exact definition of `equal` is defined by the implementation of the `areEquals` method.
 * @param <T>
 */
@FunctionalInterface
public interface Equalizer<T> {

    /**
     * Equalizers should implement `areEquals` method to define the equality definition they represent.
     * Straightforward implementations will directly compare the arguments values, and return true iif they are equals (based onb represented equality definition).
     * <p>
     * Some implementation may extract some values (members) from arguments, and compare them. This approach can be performed directly in this implementation, or can be
     * built at run-time by {@link DelegatingEqualizer} delegating equalities.
     */
    boolean areEquals(T t1, T t2);

}
