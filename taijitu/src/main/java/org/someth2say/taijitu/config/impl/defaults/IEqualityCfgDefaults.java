package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public interface IEqualityCfgDefaults<T extends IEqualityCfg> extends IEqualityCfg, ICfgDefaults<T> {
    @Override
    default String getFieldName() {
        return getDelegate().getFieldName();
    }

    @Override
    default String getFieldClass() {
        return getDelegate().getFieldClass();
    }

    @Override
    default Boolean isFieldClassStrict() {
        return getDelegate().isFieldClassStrict();
    }

    @Override
    default Object getEqualityParameters() {
        return getDelegate().getEqualityParameters();
    }
}
