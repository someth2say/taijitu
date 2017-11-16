package org.someth2say.taijitu.ui.config.impl.defaults;

import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;

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
        Boolean fieldClassStrict = getDelegate().isFieldClassStrict();
        return fieldClassStrict !=null ? fieldClassStrict: DefaultConfig.DEFAULT_FIELD_CLASS_STRICT;
    }

    @Override
    default Object getEqualityParameters() {
        return getDelegate().getEqualityParameters();
    }
}