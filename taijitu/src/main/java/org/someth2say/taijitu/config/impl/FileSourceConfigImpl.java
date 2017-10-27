package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.FileSourceConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedFileSourceConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedQuerySourceConfig;

public class FileSourceConfigImpl extends SourceConfigImpl implements ApacheBasedFileSourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final FileSourceConfig parent;

    public FileSourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                                final FileSourceConfig parent) {
        super(configuration, parent);
        this.configuration = configuration;
        this.parent = parent;
    }

    @Override
    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public FileSourceConfig getParent() {
        return parent;
    }
}
