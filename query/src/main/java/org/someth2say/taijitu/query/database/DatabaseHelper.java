package org.someth2say.taijitu.query.database;

import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.commons.database.ConnectionDataFactory;

import java.sql.Connection;

/**
 * @author Jordi Sola
 */
@Deprecated
public final class DatabaseHelper {
    private static DatabaseHelper instance;

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection(HProperties properties, String propertiesRoot, String name) throws QueryUtilsException {
        final HProperties databaseProperties = properties.getPropertiesByPrefix(propertiesRoot, name);
        ConnectionDataFactory connDataFactory = new PropertyBasedConnectionDataFactory(databaseProperties, name, propertiesRoot, name);
        return ConnectionManager.getConnection(connDataFactory);
    }
}
