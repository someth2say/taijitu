package org.someth2say.taijitu.compare.equality.aspects.internal;

public interface Equalizable<EQUALIZED> {
    /**
     * This method is kept for backwards compatibility with classical Object.equals definition.
     *
     * Default implementation should delegate to `equalsTo` method after type-checking the object parameter.
     *
     * @param obj
     * @return
     */
    @Override
	boolean equals(Object obj);

    /**
     * Method defining the actual internal equality semantics.
     *
     * @param obj
     * @return
     */
    boolean equalsTo(EQUALIZED obj);

}
