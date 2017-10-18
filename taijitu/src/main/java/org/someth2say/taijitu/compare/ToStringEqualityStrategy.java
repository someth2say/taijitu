package org.someth2say.taijitu.compare;

//TODO: Null safety
public class ToStringEqualityStrategy<T> implements EqualityStrategy<T> {

    @Override
    public int computeHashCode(T object) {
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
