package org.someth2say.taijitu.compare.equality.value;

public class ObjectToString extends AbstractConfigurableComparableCategorizerEquality<Object> {

    public ObjectToString() {
        this(null);
    }

    public ObjectToString(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(Object object) {
        return object.toString().hashCode();
    }

    @Override
    public boolean equals(Object object1, Object object2) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(Object object1, Object object2) {
        return object1.toString().compareTo(object2.toString());
    }

}
