package org.someth2say.taijitu.query.database;

import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.commons.database.ConnectionDataFactory;

import static org.someth2say.taijitu.query.properties.PropertiesLabels.*;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
@Deprecated
public class PropertyBasedConnectionDataFactory implements ConnectionDataFactory {

    private final HProperties properties;
    private final String connectionKey;
    private final String propertiesRoot;
    private final String[] propertiesSections;

    public PropertyBasedConnectionDataFactory(HProperties properties, String connectionKey, String propertiesRoot, String... propertiesSections) {
        this.properties = properties;
        this.connectionKey = connectionKey;
        this.propertiesRoot = propertiesRoot;
        this.propertiesSections = propertiesSections;
    }

    @Override
    public String getConnectionKey() {
        return connectionKey;
    }

    @Override
    public String getDriver() {
        final Object property = properties.getHierarchycalProperty(DRIVER, propertiesRoot, propertiesSections);
        return property != null ? property.toString() : null;
    }

    @Override
    public String getConnectionString() {
        final Object property = properties.getHierarchycalProperty(CONNECTION_STR, propertiesRoot, propertiesSections);
        return property != null ? property.toString() : null;

    }

    @Override
    public String getUsername() {
        final Object property = properties.getHierarchycalProperty(USERNAME, propertiesRoot, propertiesSections);
        return property != null ? property.toString() : null;
    }

    @Override
    public String getPassword() {
        final Object property = properties.getHierarchycalProperty(PASSWORD, propertiesRoot, propertiesSections);
        return property != null ? property.toString() : null;
    }
}
