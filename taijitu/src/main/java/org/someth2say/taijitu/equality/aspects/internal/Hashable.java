package org.someth2say.taijitu.equality.aspects.internal;


public interface Hashable<HASHED> extends Equalizable<HASHED> {
    @Override
    int hashCode();
}
