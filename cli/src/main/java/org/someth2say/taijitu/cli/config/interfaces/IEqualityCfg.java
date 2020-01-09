package org.someth2say.taijitu.cli.config.interfaces;

import org.someth2say.taijitu.cli.config.ConfigurationLabels;

import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_FIELD_CLASS_STRICT;

public interface IEqualityCfg extends ICfg, INamedCfg {

    default String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.FIELD_NAME);
    }

    default String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.FIELD_CLASS);
    }

    default Boolean isFieldClassStrict() {
        return getConfiguration().getBoolean(ConfigurationLabels.FIELD_CLASS_STRICT, DEFAULT_FIELD_CLASS_STRICT);
    }
}
