package org.someth2say.taijitu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.someth2say.taijitu.util.StringUtil;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class TestUtils {

    public static final String PROPERTIES_ROOT = "database";
    private static final Set<String> tableRegister = new HashSet<>();

    private TestUtils() {
    }

    public static Properties makeH2DatabaseProps(String dbName, String dbUser, String dbPwd) {
        Properties result = new Properties();
        //result.putInSections("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", PROPERTIES_ROOT, dbName, "connectionString");
        //result.putInSections("org.h2.Driver", PROPERTIES_ROOT, dbName, "driver");
        //result.putInSections(dbPwd, PROPERTIES_ROOT, dbName, "password");
        //result.putInSections(dbUser, PROPERTIES_ROOT, dbName, "username");

        result.setProperty("dataSource.user", dbUser);
        result.setProperty("dataSource.password", dbPwd);
        result.setProperty("jdbcUrl", "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        //result.put("dataSource.cachePrepStmts","true");
        //result.put("dataSource.prepStmtCacheSize","250");
        //result.put("dataSource.prepStmtCacheSqlLimit","2048");

        return result;
    }

    public static void createTable(Connection conn, String tableName, String[] fieldDefs, String[][] fieldValues) throws SQLException {
        //Create the table
        conn.createStatement().executeUpdate("CREATE TABLE " + tableName + " (" + StringUtil.join(fieldDefs) + ");");

        //Insert values
        for (String[] fieldValue : fieldValues) {
            conn.createStatement().executeUpdate("INSERT INTO " + tableName + " VALUES(" + StringUtil.join(fieldValue) + ");");
        }

        registerTable(tableName);
    }

    private static void registerTable(String tableName) {
        tableRegister.add(tableName);
    }

    public static void dropRegisteredTables(Connection conn) throws SQLException {
        for (String table : tableRegister) {
            conn.createStatement().executeUpdate("DROP TABLE IF EXISTS " + table + ";");
        }
        tableRegister.clear();
    }
}
