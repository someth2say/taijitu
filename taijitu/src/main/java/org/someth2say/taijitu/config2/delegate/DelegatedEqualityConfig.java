package org.someth2say.taijitu.config2.delegate;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config2.EqualityConfig;

public class DelegatedEqualityConfig extends DelegatedNamedConfig implements EqualityConfig {

    public DelegatedEqualityConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getFieldName() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_NAME);
    }

    @Override public String getFieldClass() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FIELD_CLASS);
    }

    @Override
    public boolean fieldClassStrict() {
        return getConfiguration().getBoolean(ConfigurationLabels.Comparison.FIELD_CLASS_STRICT, DefaultConfig.DEFAULT_FIELD_CLASS_STRICT);
    }

    @Override
    public String getEqualityParameters() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.EQUALITY_PARAMS);
    }

}
