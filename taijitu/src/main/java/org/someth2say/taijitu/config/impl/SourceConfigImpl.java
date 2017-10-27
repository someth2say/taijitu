package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedSourceConfig;

public abstract class SourceConfigImpl extends NamedConfig implements ApacheBasedSourceConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final SourceConfig parent;

    public SourceConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                            final SourceConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    @Override
    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public SourceConfig getParent() {
        return parent;
    }

}
