package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public class CaseInsensitiveValueEquality implements ValueEquality<String> {

    public static String NAME = "caseInsensitive";

    @Override
    public int computeHashCode(String object, Object equalityConfig) {
        return object.toUpperCase().hashCode();
    }

    @Override
    public boolean equals(String object1, String object2, Object equalityConfig) {
        return object1.toUpperCase().equals(object2.toUpperCase());
    }

    @Override
    public int compare(String object1, String object2, Object equalityConfig) {
        return object1.toUpperCase().compareTo(object2.toUpperCase());
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> CaseInsensitiveValueEquality.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
