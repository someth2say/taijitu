package org.someth2say.taijitu.compare.equality.value;

public class ObjectToString<T extends Object> extends AbstractConfigurableComparableCategorizerEquality<T> {

    public ObjectToString() {
        this(null);
    }

    public ObjectToString(T equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(T object) {
        return object.toString().hashCode();
    }

    @Override
    public boolean equals(T object1, T object2) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(T object1, T object2) {
        return object1.toString().compareTo(object2.toString());
    }

}
