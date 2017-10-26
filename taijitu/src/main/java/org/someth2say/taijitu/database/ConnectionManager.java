package org.someth2say.taijitu.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.someth2say.taijitu.config.DatabaseConfig;

/**
 * @author Jordi Sola
 */
public final class ConnectionManager {
    private static final Map<DatabaseConfig, HikariDataSource> datasourceMap = new HashMap<>();

    private ConnectionManager() {
    }

    public static void closeAllDataSources() {
        for (Entry<DatabaseConfig, HikariDataSource> entry : datasourceMap.entrySet()) {
            entry.getValue().close();
        }
    }

    public static Connection getConnection(DatabaseConfig databaseConfig) throws SQLException {
        HikariDataSource dataSource = datasourceMap.computeIfAbsent(databaseConfig, config -> new HikariDataSource(new HikariConfig(config.getDatabaseProperties())));
        return dataSource.getConnection();
    }

}
