package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;

public class JavaObject<T extends Object> extends AbstractConfigurableEqualizer<T> implements HasherEqualizer<T> {

    public JavaObject() {
        this(null);
    }

    public JavaObject(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(T object) {
        return object.hashCode();
    }

    @Override
    public boolean equals(T object1, T object2) {
        return object1.equals(object2);
    }

}
