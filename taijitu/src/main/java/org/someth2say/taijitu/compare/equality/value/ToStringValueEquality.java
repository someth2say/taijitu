package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public class ToStringValueEquality extends AbstractComparableValueEquality<Object> {

    public static String NAME = "toString";

    public ToStringValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int computeHashCode(Object object) {
        return object.toString().hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(Object object1, Object object2) {
        return object1.toString().compareTo(object2.toString());
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> ToStringValueEquality.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
