package org.someth2say.taijitu.query.database;

//import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.commons.database.ConnectionDataFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jordi Sola
 */
public final class ConnectionManager {
    private static final Map<String, Connection> connectionCache = new HashMap<>();
//    private static Logger log = Logger.getLogger(ConnectionManager.class);

    private ConnectionManager() {
    }

    public static void closeConnections() throws QueryUtilsException {

        for (Entry<String, Connection> entry : connectionCache.entrySet()) {
            try {
                entry.getValue().close();
            } catch (SQLException e) {
                throw new QueryUtilsException("Problems closing connection for " + entry.getKey(), e);
            }
        }

    }

    private static Connection getNewConnection(ConnectionDataFactory df) throws QueryUtilsException {
        Connection connection = null;
        String driver = df.getDriver();
        if (driver != null) {
            String connectionString = df.getConnectionString();
            String username = df.getUsername();
            String password = df.getPassword();
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                throw new QueryUtilsException("Unable to find driver class " + driver + " Please review database properties for " + df.getConnectionKey(), e);
            }
            try {
                connection = DriverManager.getConnection(connectionString, username, password);
            } catch (SQLException e) {
                throw new QueryUtilsException("Unable to establish connection to " + df.getConnectionKey(), e);
            }
        }

        return connection;
    }

    public static Connection getConnection(ConnectionDataFactory df) throws QueryUtilsException {
        Connection connection = connectionCache.get(df.getConnectionKey());
        try {
            if (connection == null || connection.isClosed()) {
                connection = getNewConnection(df);
                connectionCache.put(df.getConnectionKey(), connection);
            }
        } catch (SQLException e) {
            throw new QueryUtilsException("Unable to open connection for " + df.getConnectionKey(), e);
        }

        return connection;
    }

    public static Connection getConnection(HProperties properties, String propertiesRoot, String name) throws QueryUtilsException {
        return DatabaseHelper.getInstance().getConnection(properties, propertiesRoot, name);
    }

}
