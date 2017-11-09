package org.someth2say.taijitu.compare.equality.value;

public abstract class AbstractComparableValueEquality<T> extends AbstractValueEquality<T> implements ComparableValueEquality<T> {

    public AbstractComparableValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }
}
