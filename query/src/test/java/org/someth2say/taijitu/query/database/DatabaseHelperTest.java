package org.someth2say.taijitu.query.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.someth2say.TestUtils;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.QueryUtilsException;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

/**
 * @author Jordi Sola
 */
public class DatabaseHelperTest {
    private static final String DB_NAME = "test";
    private static final String DB_USER = "user";
    private static final String DB_PWD = "pwd";

    @Before
    public void setUp() throws Exception {
        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        final PropertyBasedConnectionDataFactory df = new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME);
        try (final Connection conn = ConnectionManager.getConnection(df)) {
            conn.createStatement().executeUpdate("CREATE TABLE data (key VARCHAR(255) PRIMARY KEY, value VARCHAR(1023) )");
        }
    }

    @Test
    public void getConnection() throws Exception {
        HProperties properties = new HProperties();
        properties.putInSections("jdbc:h2:mem:test", TestUtils.PROPERTIES_ROOT, "test", "connectionString");
        properties.putInSections("org.h2.Driver", TestUtils.PROPERTIES_ROOT, "test", "driver");
        properties.putInSections("pwd", TestUtils.PROPERTIES_ROOT, "test", "password");
        properties.putInSections("user", TestUtils.PROPERTIES_ROOT, "test", "username");

        final Connection connection = DatabaseHelper.getInstance().getConnection(properties, TestUtils.PROPERTIES_ROOT, "test");
        assertNotNull(connection);
        connection.close();
    }

    @After
    public void dropTables() throws SQLException, QueryUtilsException {

        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        // Create the tables and test data
        final PropertyBasedConnectionDataFactory df = new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME);
        try (Connection conn = ConnectionManager.getConnection(df)) {
            TestUtils.dropRegisteredTables(conn);
        }
    }


}