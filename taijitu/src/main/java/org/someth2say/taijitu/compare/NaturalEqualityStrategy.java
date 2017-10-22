package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.EqualityConfig;

//TODO: Null safety
public class NaturalEqualityStrategy implements EqualityStrategy {

    public static String NAME = "natural";

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
                return NaturalEqualityStrategy.NAME;
            }
        };
    }

    @Override
    public int computeHashCode(Object object, Object equalityConfig) {
        return object.hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2, Object equalityConfig) {
        return object1.equals(object2);
    }

    @Override
    public int compare(Object object1, Object object2, Object equalityConfig) {
        return 0;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
