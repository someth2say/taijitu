package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;

public abstract class AbstractConfigurableComparableCategorizerEquality<T> extends AbstractConfigurableEquality<T> implements ComparableCategorizerEquality<T> {

    AbstractConfigurableComparableCategorizerEquality(Object equalityConfig) {
        super(equalityConfig);
    }

}
