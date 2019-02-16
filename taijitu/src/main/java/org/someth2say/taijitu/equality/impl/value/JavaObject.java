package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Hasher;

public class JavaObject<T> extends AbstractConfigurableEqualizer<T> implements Hasher<T> {

    public static final JavaObject<Object> EQUALITY = new JavaObject<>();

    public JavaObject() {
        this(null);
    }

    public JavaObject(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hash(T object) {
        return object.hashCode();
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        return object1.equals(object2);
    }

}
