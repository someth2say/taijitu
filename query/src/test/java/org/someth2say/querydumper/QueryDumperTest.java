package org.someth2say.querydumper;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.someth2say.TestUtils;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.query.database.PropertyBasedConnectionDataFactory;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.queryactions.QueryDumper;
import org.someth2say.taijitu.commons.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jordi Sola
 */
public class QueryDumperTest {
    private static final String DB_NAME = "test";
    private static final String DB_USER = "user";
    private static final String DB_PWD = "pwd";
    private HProperties properties;

    @BeforeClass
    public static void initLogging() {
        LogUtils.addConsoleAppenderToRootLogger(Level.ERROR, LogUtils.DEFAULT_PATTERN);
    }

    @Before
    public void setUp() throws Exception {
        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        try (Connection conn = ConnectionManager.getConnection(new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME))) {
            String tableName = "test";
            String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
            String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}, {"3", "'TRES'"}, {"4", "'CUATRO'"}, {"5", "'CINCO'"}};
            TestUtils.createTable(conn, tableName, columnDefs, columnValues);
        }
        properties = new HProperties();
        properties.putAll(databaseProps);
        properties.put("querydumper.connection", "test");
        properties.put("querydumper.sql", "SELECT * FROM test;");
    }

    @After
    public void tearDown() throws Exception {
        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        // Create the tables and test data
        try (Connection conn = ConnectionManager.getConnection(new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME))) {
            TestUtils.dropRegisteredTables(conn);
        }

        final File file = new File("dump.txt");
        if (file.exists()) {
            file.delete();
        }

    }


    @Test
    public void dumpTest() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QueryDumper.dump(properties, outputStream);
            final String string = outputStream.toString();
            assertTrue(string.contains("1"));
            assertTrue(string.contains("UNO"));
            assertTrue(string.contains("5"));
            assertTrue(string.contains("CINCO"));
        } catch (QueryUtilsException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void dumpToFileTest() {
        final File file = new File("dump.txt");
        if (file.exists()) {
            file.delete();
        }

        try {
            QueryDumper.dumpToFile(file, properties);
            assertTrue(file.exists());
        } catch (QueryUtilsException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}