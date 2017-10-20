package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedDatabaseConfig;

public class DatabaseConfigImpl extends NamedConfig implements ApacheBasedDatabaseConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    @Override
	public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public DatabaseConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
    }

}
