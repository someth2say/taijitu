package org.someth2say.taijitu.config.impl.apache;

import org.someth2say.taijitu.config.DatabaseConfig;

import java.util.Iterator;
import java.util.Properties;

public interface ApacheBasedDatabaseConfig extends ApacheBasedConfig, DatabaseConfig {

    @Override
    default Properties getDatabaseProperties() {
        Properties result = new Properties();
        // Why Configuration is not iterable?!
        final Iterator<String> keys = getConfiguration().getKeys();
        while (keys.hasNext()){
            final String key = keys.next();
            final Object property = getConfiguration().getProperty(key);
            result.setProperty(key, property.toString());
        }
        return result;
    }

}