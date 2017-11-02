package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public interface DefaultEqualityConfigIface extends IEqualityCfg {
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
