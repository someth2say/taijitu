package org.someth2say.taijitu.config2.delegate;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.SourceConfig;

import java.util.List;

public interface ApacheBasedSourceConfig extends SourceConfig, ApacheDelegatedConfig {

    SourceConfig getParent();

    @Override
    default List<String> getKeyFields() {
        List<String> keys = getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
        return keys != null ? keys : getParent() != null ? getParent().getKeyFields() : null;
    }

    @Override
    default String getType() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
        return statement != null ? statement : getParent() != null ? getParent().getType() : null;
    }
}
