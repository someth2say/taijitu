package org.someth2say.taijitu.compare.equality.aspects.internal;


public interface Hashable<HASHED> extends Equalizable<HASHED> {
    @Override
    int hashCode();
}
