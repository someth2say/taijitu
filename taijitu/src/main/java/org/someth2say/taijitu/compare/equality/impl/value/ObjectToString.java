package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorCategorizerEquality;

public class ObjectToString<T extends Object> extends AbstractConfigurableEquality<T> implements ComparatorCategorizerEquality<T> {

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
