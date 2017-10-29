package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;

import java.util.Iterator;
import java.util.Properties;

public class DatabaseConfigImpl implements DatabaseConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public DatabaseConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Properties getDatabaseProperties() {
        Properties result = new Properties();
        //TODO: This dumps ALL configuration entries to the properties. Maybe worth filtering out...
        final Iterator<String> keys = getConfiguration().getKeys();
        while (keys.hasNext()){
            final String key = keys.next();
            final Object property = getConfiguration().getProperty(key);
            result.setProperty(key, property.toString());
        }
        return result;
    }
}
