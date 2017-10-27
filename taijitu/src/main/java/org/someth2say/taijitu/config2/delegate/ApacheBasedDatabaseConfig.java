package org.someth2say.taijitu.config2.delegate;

import org.someth2say.taijitu.config.DatabaseConfig;

import java.util.Iterator;
import java.util.Properties;

public interface ApacheBasedDatabaseConfig extends ApacheDelegatedConfig, DatabaseConfig {

    @Override
    default Properties getDatabaseProperties() {
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
