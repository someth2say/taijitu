package org.someth2say.taijitu.config2.delegate;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.FileSourceConfig;

public interface ApacheBasedFileSourceConfig extends ApacheDelegatedConfig, FileSourceConfig {

    FileSourceConfig getParent();

    @Override
    default String getPath() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.FILE_PATH);
        return statement != null ? statement : getParent().getPath();
    }
}
