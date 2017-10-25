package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.EqualityConfig;

public interface DefaultEqualityConfig extends EqualityConfig {
    @Override
    default String getFieldName() {
        return null;
    }

    @Override
    default String getFieldClass() {
        return null;
    }

    @Override
    default boolean fieldClassStrict() {
        return false;
    }

    @Override
    default Object getEqualityParameters() {
        return null;
    }

}
