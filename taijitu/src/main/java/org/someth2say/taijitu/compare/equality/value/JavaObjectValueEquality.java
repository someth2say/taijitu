package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.config.interfaces.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public class JavaObjectValueEquality extends AbstractValueEquality<Object> {

    public static String NAME = "object";

    public JavaObjectValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int computeHashCode(Object object) {
        return object.hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2) {
        return object1.equals(object2);
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> JavaObjectValueEquality.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
