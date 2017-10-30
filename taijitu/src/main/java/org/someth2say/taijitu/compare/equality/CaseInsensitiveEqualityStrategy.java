package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.delegating.EqualityConfigIface;

public class CaseInsensitiveEqualityStrategy implements EqualityStrategy<String> {

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

    public static EqualityConfigIface defaultConfig() {
        return (DefaultEqualityConfigIface) () -> CaseInsensitiveEqualityStrategy.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
