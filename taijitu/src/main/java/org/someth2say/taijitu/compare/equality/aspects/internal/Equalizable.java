package org.someth2say.taijitu.compare.equality.aspects.internal;

public interface Equalizable<EQUALIZED> {
    @Override
	boolean equals(Object obj);

    boolean equalsTo(EQUALIZED obj);

}
