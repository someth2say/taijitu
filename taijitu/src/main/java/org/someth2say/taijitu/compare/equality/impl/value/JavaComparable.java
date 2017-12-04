package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;

public class JavaComparable<T extends Comparable<T>> extends AbstractConfigurableEqualizer<T> implements ComparatorHasherEqualizer<T> {

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
