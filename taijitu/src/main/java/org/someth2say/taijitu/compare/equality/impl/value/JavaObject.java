package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

public class JavaObject<T> extends AbstractConfigurableEqualizer<T> implements Hasher<T> {

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
