package org.someth2say.taijitu.query.database;

import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.properties.HProperties;

import java.sql.Connection;

/**
 * Created by Jordi Sola on 23/02/2017.
 */
public class PropertiesBasedConnectionFactory implements IConnectionFactory {
    final HProperties properties;
    private String propertiesRoot;

    public PropertiesBasedConnectionFactory(HProperties properties, String propertiesRoot) {
        this.properties = properties;
        this.propertiesRoot = propertiesRoot;
    }

    @Override
    public Connection getConnection(String name) throws QueryUtilsException {
        return ConnectionManager.getConnection(properties, propertiesRoot, name);
    }

    @Override
    public void closeAll() throws QueryUtilsException {
        ConnectionManager.closeConnections();
    }
}
