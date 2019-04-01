package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;

import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_FIELD_CLASS_STRICT;

public interface IApacheEqualityCfg extends IApacheCfg, IEqualityCfg {

    //TODO: May use a field list here.
    @Override
    default String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.FIELD_NAME);
    }

    @Override
    default String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.FIELD_CLASS);
    }

    @Override
    default Boolean isFieldClassStrict() {
        return getConfiguration().getBoolean(ConfigurationLabels.FIELD_CLASS_STRICT, DEFAULT_FIELD_CLASS_STRICT);
    }

}
