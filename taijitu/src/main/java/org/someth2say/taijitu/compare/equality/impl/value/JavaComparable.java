package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.external.ComparatorCategorizerEquality;

public class JavaComparable<T extends Comparable<T>> extends AbstractConfigurableEquality<T> implements ComparatorCategorizerEquality<T> {

    public JavaComparable() {
        this(null);
    }
    public JavaComparable(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int compare(T object1, T other) {
        return object1.compareTo(other);
    }

    @Override
    public int hashCode(T keyValue) {
        return keyValue.hashCode();
    }

    @Override
    public boolean equals(T object1, T other) {
        return object1.equals(other);
    }
}
