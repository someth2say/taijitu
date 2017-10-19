package org.someth2say.taijitu.compare;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.EqualityConfig;

//TODO: Null safety
public class ToStringEqualityStrategy implements EqualityStrategy {

    public static String NAME = "toString";

    @Override
    public int computeHashCode(Object object, Object equalityConfig) {
        return object.toString().hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2, Object equalityConfig) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(Object object1, Object object2, Object equalityConfig) {
        return object1.toString().compareTo(object2.toString());
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
                return ToStringEqualityStrategy.NAME;
            }
        };
    }


    @Override
    public String getName() {
        return NAME;
    }
}
