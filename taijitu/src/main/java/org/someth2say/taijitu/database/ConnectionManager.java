package org.someth2say.taijitu.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Jordi Sola
 */
public final class ConnectionManager {
    private static final Map<String, HikariDataSource> datasourceMap = new HashMap<>();

    private ConnectionManager() {
    }

    public static void closeAllDataSources() {
        for (Entry<String, HikariDataSource> entry : datasourceMap.entrySet()) {
            entry.getValue().close();
        }
    }

    public static void buildDataSource(final String name, final Properties databaseConfig) {
        HikariConfig hicariConfig = new HikariConfig(databaseConfig);
        //TODO: Consider making dataSource creation lazy (say, Map<name, HicariConfig>)
        HikariDataSource dataSource = new HikariDataSource(hicariConfig);
        datasourceMap.put(name, dataSource);
    }


    public static Connection getConnection(final String name) throws SQLException {
        HikariDataSource dataSource = datasourceMap.get(name);
        return dataSource != null ? dataSource.getConnection() : null;
    }

}
