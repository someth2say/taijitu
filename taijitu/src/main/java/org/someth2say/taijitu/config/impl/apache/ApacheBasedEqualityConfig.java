package org.someth2say.taijitu.config.impl.apache;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.EqualityConfig;

public interface ApacheBasedEqualityConfig extends ApacheBasedConfig, EqualityConfig {

    @Override
    default String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_NAME);
    }

    @Override
    default String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_CLASS);
    }

    @Override
    default String getEqualityParameters() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.EQUALITY_PARAMS);
    }

}
