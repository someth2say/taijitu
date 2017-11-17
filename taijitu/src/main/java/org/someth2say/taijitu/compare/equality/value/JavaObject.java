package org.someth2say.taijitu.compare.equality.value;

public class JavaObject<T extends Object> extends AbstractConfigurableCategorizerEquality<T> {



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
