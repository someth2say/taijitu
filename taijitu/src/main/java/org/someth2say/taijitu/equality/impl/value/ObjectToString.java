package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

public class ObjectToString<T> extends AbstractConfigurableEqualizer<T> implements ComparatorHasher<T> {

    public static final ObjectToString<Object> EQUALITY = new ObjectToString<>();

    public ObjectToString() {
        this(null);
    }

    public ObjectToString(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hash(T object) {
        return object.toString().hashCode();
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        return object1.toString().equals(object2.toString());
    }

    @Override
    public int compare(T object1, T object2) {
        return object1.toString().compareTo(object2.toString());
    }

}
