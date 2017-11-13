package org.someth2say.taijitu.config.interfaces;

public interface DefaultEqualityConfig extends IEqualityCfg {
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
