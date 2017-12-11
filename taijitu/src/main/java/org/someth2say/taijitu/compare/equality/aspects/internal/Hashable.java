package org.someth2say.taijitu.compare.equality.aspects.internal;


public interface Hashable<T> extends Equalizable<T> {
    @Override
    int hashCode();
}
