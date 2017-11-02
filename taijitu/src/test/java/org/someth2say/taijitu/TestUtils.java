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

    private static final Set<String> tableRegister = new HashSet<>();

    private TestUtils() {
    }

    static Properties makeH2DatabaseProps(String dbName, String dbUser, String dbPwd) {
        Properties result = new Properties();
        result.setProperty("dataSource.user", dbUser);
        result.setProperty("dataSource.password", dbPwd);
        result.setProperty("jdbcUrl", "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        //result.setProperty("dataSource.cachePrepStmts","true");
        //result.setProperty("dataSource.prepStmtCacheSize","250");
        //result.setProperty("dataSource.prepStmtCacheSqlLimit","2048");

        return result;
    }

    static void createTable(Connection conn, String tableName, String[] fieldDefs, String[][] fieldValues) throws SQLException {
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

    static void dropRegisteredTables(Connection conn) throws SQLException {
        for (String table : tableRegister) {
            conn.createStatement().executeUpdate("DROP TABLE IF EXISTS " + table + ";");
        }
        tableRegister.clear();
    }
}
