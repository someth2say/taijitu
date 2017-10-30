package org.someth2say.taijitu.config.delegating;

import org.someth2say.taijitu.config.delegate.EqualityConfigDelegate;

public interface EqualityConfigIface extends DelegatingConfigIface<EqualityConfigDelegate>, EqualityConfigDelegate {

    default String getFieldName() {
        return getDelegate().getFieldName();
    }

    default String getFieldClass() {
        return getDelegate().getFieldClass();
    }

    default Boolean isFieldClassStrict() {
        return getDelegate().isFieldClassStrict();
    }

    default Object getEqualityParameters() {
        return getDelegate().getEqualityParameters();
    }
}
