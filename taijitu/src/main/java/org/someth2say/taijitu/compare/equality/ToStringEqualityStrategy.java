package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.EqualityConfig;

//TODO: Null safety
public class ToStringEqualityStrategy implements EqualityStrategy<Object> {

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

    //TODO: Maybe all default configs should share a common interface
    public static EqualityConfig defaultConfig() {
        return (DefaultEqualityConfig) () -> ToStringEqualityStrategy.NAME;
    }


    @Override
    public String getName() {
        return NAME;
    }
}
