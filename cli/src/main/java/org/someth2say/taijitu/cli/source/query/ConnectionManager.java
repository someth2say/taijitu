package org.someth2say.taijitu.cli.source.query;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Jordi Sola
 */
public final class ConnectionManager {
    private static final Map<Properties, HikariDataSource> datasourceMap = new HashMap<>();

    private ConnectionManager() {
    }

    public static void closeAllDataSources() {
        for (Entry<Properties, HikariDataSource> entry : datasourceMap.entrySet()) {
            entry.getValue().close();
        }
        datasourceMap.clear();
    }

    public static Connection getConnection(Properties properties) throws SQLException {
        HikariDataSource dataSource = datasourceMap.computeIfAbsent(properties, config -> new HikariDataSource(new HikariConfig(properties)));
        return dataSource.getConnection();
    }

}
