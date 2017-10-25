package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedEqualityConfig;

public class EqualityConfigImpl extends NamedConfig implements ApacheBasedEqualityConfig {

    @Override
	public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    private final ImmutableHierarchicalConfiguration configuration;

    public EqualityConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
    }

}
