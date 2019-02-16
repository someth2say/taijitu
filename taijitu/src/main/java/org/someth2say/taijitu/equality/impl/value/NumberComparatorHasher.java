package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

public class NumberComparatorHasher<T extends Number> extends JavaObject<T> implements ComparatorHasher<T> {

    public static final NumberComparatorHasher<Number> EQUALITY = new NumberComparatorHasher<>();

    @Override
    public int compare(T number1, T number2) {
        return Double.compare(number1.doubleValue(), number2.doubleValue());
    }
}
