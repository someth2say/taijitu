package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.FileSourceConfig;

public class FileSourceConfigImpl extends SourceConfigImpl implements FileSourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final FileSourceConfig parent;

    public FileSourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                                final FileSourceConfig parent) {
        super(configuration, parent);
        this.configuration = configuration;
        this.parent = parent;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public FileSourceConfig getParent() {
        return parent;
    }

    @Override
    public String getPath() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.FILE_PATH);
        return statement != null ? statement : getParent().getPath();
    }
}
