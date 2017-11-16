package org.someth2say.taijitu.compare.equality.value;

public class JavaObject extends AbstractConfigurableCategorizerEquality<Object> {



    public JavaObject() {
        this(null);
    }

    public JavaObject(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(Object object) {
        return object.hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2) {
        return object1.equals(object2);
    }

}
