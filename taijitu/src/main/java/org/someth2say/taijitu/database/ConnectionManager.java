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
    // TODO: Should be moved to DataSourceManager, mapping datasources to names, and
    // delegating connection pooling to an specialist (HicariCP?)

    private static final Map<String, HikariDataSource> datasourceMap = new HashMap<>();

    private ConnectionManager() {
    }

    public static void closeAllDataSources() throws QueryUtilsException {
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
