package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.delegating.EqualityConfigIface;

public interface DefaultEqualityConfigIface extends EqualityConfigIface {
    @Override
    default String getFieldName() {
        return null;
    }

    @Override
    default String getFieldClass() {
        return null;
    }

    @Override
    default Boolean isFieldClassStrict() {
        return false;
    }

    @Override
    default Object getEqualityParameters() {
        return null;
    }

}
