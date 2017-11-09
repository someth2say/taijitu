package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public class JavaComparableValueEquality<T extends Comparable<T>> extends AbstractComparableValueEquality<T> {

    public static String NAME = "comparable";

    public JavaComparableValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> JavaComparableValueEquality.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int compare(T object1, T other) {
        return object1.compareTo(other);
    }

    @Override
    public int computeHashCode(T keyValue) {
        return keyValue.hashCode();
    }

    @Override
    public boolean equals(T object1, T other) {
        return object1.equals(other);
    }
}
