package org.someth2say.taijitu.ui.config.delegates.apache.defaults;

import org.someth2say.taijitu.ui.config.ConfigurationLabels;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;

import java.util.NoSuchElementException;

public interface ApacheEqualityCfgDefaults extends ApacheCfgDefaults, IEqualityCfg {

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
        try {
            return getConfiguration().getBoolean(ConfigurationLabels.FIELD_CLASS_STRICT);
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    default Object getEqualityParameters() {
        return getConfiguration().getString(ConfigurationLabels.EQUALITY_PARAMS);
    }
}
