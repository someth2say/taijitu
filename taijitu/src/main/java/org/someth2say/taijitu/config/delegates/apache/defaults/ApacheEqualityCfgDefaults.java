package org.someth2say.taijitu.config.delegates.apache.defaults;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public interface ApacheEqualityCfgDefaults extends ApacheCfgDefaults, IEqualityCfg {

    @Override
    default String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_NAME);
    }

    @Override
    default String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_CLASS);
    }

    @Override
    default Boolean isFieldClassStrict() {
        return getConfiguration().getBoolean(ConfigurationLabels.Comparison.FIELD_CLASS_STRICT);
    }

    @Override
    default Object getEqualityParameters() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.EQUALITY_PARAMS);
    }
}
