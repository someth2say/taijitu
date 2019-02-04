package org.someth2say.taijitu.compare.equality.aspects.external;

public interface Equalizer<EQUALIZED> {

    /**
     * Equalizers should implement `areEquals` method to define the equality definition they represent.
     * Straightforward implementations will directly compare the arguments values, and return true iif they are equals (based onb represented equality definition).
     * <p>
     * Some implementation may extract some values (members) from arguments, and compare them. This approach can be performed directly in this implementation, or can be
     * built at run-time by partial equalities.
     */
    boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2);


}
