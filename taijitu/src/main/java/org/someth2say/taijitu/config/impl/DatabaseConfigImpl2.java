package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.NamedConfig;

import java.util.Iterator;
import java.util.Properties;

public class DatabaseConfigImpl2 extends NamedConfig implements DatabaseConfig {

    private final ImmutableHierarchicalConfiguration configuration;

    public DatabaseConfigImpl2(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
    }

    @Override
    public Properties getAsProperties() {
        Properties result = new Properties();
        // Why Configuration is not iterable?!
        final Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()){
            result.putAll(configuration.getProperties(keys.next()));
        }
        return result;
    }

}
