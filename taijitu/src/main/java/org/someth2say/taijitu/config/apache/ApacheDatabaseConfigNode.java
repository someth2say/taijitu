package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;

import java.util.Iterator;
import java.util.Properties;

public class ApacheDatabaseConfigNode extends ApacheBasedConfigNode implements DatabaseConfig {
    public ApacheDatabaseConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheQuerySourceConfigNode parent) {
        super(configuration,parent);
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
