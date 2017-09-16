package org.someth2say;

import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.commons.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class TestUtils {

    public static final String PROPERTIES_ROOT = "database";
    private static final Set<String> tableRegister = new HashSet<>();

    private TestUtils() {
    }

    public static HProperties makeH2DatabaseProps(String dbName, String dbUser, String dbPwd) {
        HProperties result = new HProperties();
        result.putInSections("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", PROPERTIES_ROOT, dbName, "connectionString");
        result.putInSections("org.h2.Driver", PROPERTIES_ROOT, dbName, "driver");
        result.putInSections(dbPwd, PROPERTIES_ROOT, dbName, "password");
        result.putInSections(dbUser, PROPERTIES_ROOT, dbName, "username");
        return result;
    }

    public static void createTable(Connection conn, String tableName, String[] columnDefs, String[][] columnValues) throws SQLException {
        //Create the table
        conn.createStatement().executeUpdate("CREATE TABLE " + tableName + " (" + StringUtil.join(columnDefs) + ");");

        //Insert values
        for (String[] columnValue : columnValues) {
            conn.createStatement().executeUpdate("INSERT INTO " + tableName + " VALUES(" + StringUtil.join(columnValue) + ");");
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
