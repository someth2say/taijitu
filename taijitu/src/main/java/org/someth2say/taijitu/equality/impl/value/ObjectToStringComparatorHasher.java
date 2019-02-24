package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

public class ObjectToStringComparatorHasher<T> implements ComparatorHasher<T> {

    public static final ObjectToStringComparatorHasher INSTANCE = new ObjectToStringComparatorHasher();

    @Override
    public int hash(T object) {
        return object.toString().hashCode();
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(T object1, T object2) {
        return object1.toString().compareTo(object2.toString());
    }

}
