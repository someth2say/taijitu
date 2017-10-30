package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.delegate.EqualityConfigDelegate;

public class ApacheEqualityConfig extends ApacheNamedConfig implements EqualityConfigDelegate {

    ApacheEqualityConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_NAME);
    }

    @Override
    public String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_CLASS);
    }

    @Override
    public Boolean isFieldClassStrict() {
        return getConfiguration().getBoolean(ConfigurationLabels.Comparison.FIELD_CLASS_STRICT, DefaultConfig.DEFAULT_FIELD_CLASS_STRICT);
    }

    @Override
    public Object getEqualityParameters() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.EQUALITY_PARAMS);
    }
}
