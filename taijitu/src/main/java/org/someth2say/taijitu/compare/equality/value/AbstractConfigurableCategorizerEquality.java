package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.CategorizerEquality;

public abstract class AbstractConfigurableCategorizerEquality<T> extends AbstractConfigurableEquality<T> implements CategorizerEquality<T> {

    public AbstractConfigurableCategorizerEquality(Object equalityConfig) {
        super(equalityConfig);
    }

}
