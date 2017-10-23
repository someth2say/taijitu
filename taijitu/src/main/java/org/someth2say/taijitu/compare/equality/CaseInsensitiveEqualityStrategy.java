package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.EqualityConfig;

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


    public static EqualityConfig defaultConfig() {
        return new EqualityConfig() {
            @Override
            public String getFieldName() {
                return null;
            }

            @Override
            public String getFieldClass() {
                return null;
            }

            @Override
            public Object getEqualityParameters() {
                return null;
            }

            @Override
            public String getName() {
                return CaseInsensitiveEqualityStrategy.NAME;
            }
        };
    }


    @Override
    public String getName() {
        return NAME;
    }
}
