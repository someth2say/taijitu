package org.someth2say.taijitu.config2.hierarchy;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.FileSourceConfig;

public interface ApacheBasedFileSourceConfig extends ApacheBasedConfig, FileSourceConfig {

    FileSourceConfig getParent();

    @Override
    default String getPath() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.FILE_PATH);
        return statement != null ? statement : getParent().getPath();
    }
}
