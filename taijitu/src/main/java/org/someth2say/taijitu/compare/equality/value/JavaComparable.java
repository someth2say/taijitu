package org.someth2say.taijitu.compare.equality.value;

public class JavaComparable<T extends Comparable<T>> extends AbstractConfigurableComparableCategorizerEquality<T> {

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
