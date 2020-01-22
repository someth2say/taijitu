package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

public class ComparableComparatorHasher<TYPE extends Comparable<TYPE>> implements ComparatorHasher<TYPE> {

    @SuppressWarnings({"rawtypes"})
    public static ComparableComparatorHasher INSTANCE = new ComparableComparatorHasher<>();

    @Override
    public int compare(TYPE object1, TYPE other) {
        return object1.compareTo(other);
    }

    @Override
    public int hash(TYPE keyValue) {
        return keyValue.hashCode();
    }

    @Override
    public boolean areEquals(TYPE object1, TYPE other) {
        return object1.equals(other);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
