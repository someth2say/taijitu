package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Hasher;

public class ObjectHasher<T> implements Hasher<T> {

    public static final ObjectHasher<Object> INSTANCE = new ObjectHasher<>();

    @Override
    public int hash(T object) {
        return object.hashCode();
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        return object1.equals(object2);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
