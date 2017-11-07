package org.someth2say.taijitu.compare.equality.value;

public abstract class AbstractSortedValueEquality<T> extends AbstractValueEquality<T> implements SortedValueEquality<T> {

    public AbstractSortedValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }
}
