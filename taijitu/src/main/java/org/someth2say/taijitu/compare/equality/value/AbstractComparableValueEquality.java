package org.someth2say.taijitu.compare.equality.value;

abstract class AbstractComparableValueEquality<T> extends AbstractValueEquality<T> implements ComparableValueEquality<T> {

    AbstractComparableValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }
}
