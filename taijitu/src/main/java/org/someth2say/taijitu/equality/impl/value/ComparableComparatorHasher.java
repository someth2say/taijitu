package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

public class ComparableComparatorHasher<T extends Comparable<T>> implements ComparatorHasher<T> {

    public static ComparableComparatorHasher INSTANCE = new ComparableComparatorHasher();

    @Override
    public int compare(T object1, T other) {
        return object1.compareTo(other);
    }

    @Override
    public int hash(T keyValue) {
        return keyValue.hashCode();
    }

    @Override
    public boolean areEquals(T object1, T other) {
        return object1.equals(other);
    }

}
