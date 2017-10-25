package org.someth2say.taijitu;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.compare.equality.CaseInsensitiveEqualityStrategy;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.equality.ValueThresholdEqualityStrategy;
import org.someth2say.taijitu.compare.equality.TimestampThresholdEqualityStrategy;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.database.ConnectionManager;
import org.someth2say.taijitu.database.QueryUtilsException;
import org.someth2say.taijitu.strategy.mapping.MappingStrategy;
import org.someth2say.taijitu.strategy.sorted.SortedStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.someth2say.taijitu.config.ConfigurationLabels.Comparison.*;
import static org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields.KEY;
import static org.someth2say.taijitu.config.ConfigurationLabels.Sections.COMPARISON;


/**
 * @author Jordi Sola
 */
@RunWith(Parameterized.class)
public class TaijituTest {

    private static final String DB_NAME = "test";
    private static final String DB_USER = "user";
    private static final String DB_PWD = "pwd";
    private final String strategyName;

    public TaijituTest(final String strategyName) {
        this.strategyName = strategyName;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection strategies() {
        return Arrays.asList(
                SortedStrategy.NAME,
                MappingStrategy.NAME
        );
    }

    private static Connection getConnection(String dbName, Properties databaseProps) throws SQLException {
        ConnectionManager.buildDataSource(dbName, databaseProps);
        return ConnectionManager.getConnection(dbName);
    }

    @After
    public void dropTables() throws SQLException, QueryUtilsException {
        Connection conn = getConnection(DB_NAME, TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD));
        TestUtils.dropRegisteredTables(conn);
    }

    @Test
    public void genericTest() throws QueryUtilsException, TaijituException, SQLException, ConfigurationException, InterruptedException {
        // Create the tables and test data

        final Properties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        Connection conn = getConnection(DB_NAME, databaseProps); // This generate the DB in H2

        String[] commonSchema = {
                "key INTEGER(5) PRIMARY KEY",
                "char CHARACTER(1)",
                "string VARCHAR(254)",
                "bool BOOLEAN",
                "small SMALLINT",// 	Integer numerical (no decimal). Precision 5
                "medium INTEGER",// 	Integer numerical (no decimal). Precision 10
                "big BIGINT",// 	Integer numerical (no decimal). Precision 19
                "dec DECIMAL(5,3)",// 	Exact numerical, precision p, scale s. Example: decimal(5,2) is a number that has 3 digits before the decimal and 2 digits after the decimal
                "num NUMERIC(5,3)",// 	Exact numerical, precision p, scale s. (Same as DECIMAL)
                "flop FLOAT(2)",// 	Approximate numerical, mantissa precision p. A floating number in base 10 exponential notation. The size argument for this type consists of a single number specifying the minimum precision
                "rea REAL",// 	Approximate numerical, mantissa precision 7
                "flo FLOAT",// 	Approximate numerical, mantissa precision 16
                "dou DOUBLE PRECISION",// 	Approximate numerical, mantissa precision 16
                "dat DATE",// 	Stores year, month, and day values
                "tim TIME",// 	Stores hour, minute, and second values
                "timest TIMESTAMP"// 	Stores year, month, day, hour, minute, and second values
        };

        String[][] commonValues = {
                {"1", "'A'", "'STRING'", "true", "10000", "1000000000", "1000000000000000000", "23.456", "89.102", "34.56", "78.9012345", "67.8901234567890123", "45.6789012345678901", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP"},
                {"2", "'B'", "'STRING'", "true", "10000", "1000000000", "1000000000000000000", "23.456", "89.102", "34.56", "78.9012345", "67.8901234567890123", "45.6789012345678901", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP"}
        };
        TestUtils.createTable(conn, "test", commonSchema, commonValues);
        // introduce some differences..
        commonValues[0][13] = "CURRENT_DATE+1";
        commonValues[1][9] ="34.57"; // This one should be absorbed by threshold
        TestUtils.createTable(conn, "test2", commonSchema, commonValues);

        conn.close();


        final PropertiesConfiguration config = new BasicConfigurationBuilder<>(PropertiesConfiguration.class).getConfiguration();
        //Databases
        putAll(config, databaseProps, DATABASE_REF + "." + DB_NAME + ".");
        // Comparisons
        putAll(config, makeComparisonProps("test1", "KEY", "select * from test", "select * from test", "test"), "");
        putAll(config, makeComparisonProps("test2", "KEY", "select * from test", "select * from test2", "test"), "");

        // Disable plugins, 'cause we need to write nothing.
        commonPropertiesSetup(config);

        //Add comparators
        //Case insensitive strings
        config.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + CaseInsensitiveEqualityStrategy.NAME + "." + ConfigurationLabels.Comparison.FIELD_CLASS, String.class.getName());
        //Threshold 0.1 for Numbers
        config.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + ValueThresholdEqualityStrategy.NAME + "." + ConfigurationLabels.Comparison.FIELD_CLASS, Number.class.getName());
        config.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + ValueThresholdEqualityStrategy.NAME + "." + ConfigurationLabels.Comparison.EQUALITY_PARAMS, "0.01");
        //Threshold 0.1s for dates.
        config.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + TimestampThresholdEqualityStrategy.NAME + "." + ConfigurationLabels.Comparison.FIELD_CLASS, Timestamp.class.getName());
        config.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + TimestampThresholdEqualityStrategy.NAME + "." + ConfigurationLabels.Comparison.EQUALITY_PARAMS, "100");

        final ComparisonResult[] comparisonResults = new Taijitu().compare(ConfigurationUtils.unmodifiableConfiguration(ConfigurationUtils.convertToHierarchical(config)));

        //assertEquals(2, comparisonResults.length);
        final ComparisonResult firstResult = comparisonResults[0];
        assertEquals(0, firstResult.getDisjoint().size());
        assertEquals(0, firstResult.getDifferent().size());
        final ComparisonResult secondResult = comparisonResults[1];
        assertEquals(0, secondResult.getDisjoint().size());
        assertEquals(1, secondResult.getDifferent().size());
    }

    private void putAll(final PropertiesConfiguration configuration, final Properties props, final String keyPrefix) {
        props.forEach((o, o2) -> configuration.addProperty(keyPrefix + o.toString(), o2));
    }

    private void commonPropertiesSetup(PropertiesConfiguration testProperties) {
        testProperties.setProperty(ConfigurationLabels.Comparison.STRATEGY, strategyName);
        testProperties.setProperty(ConfigurationLabels.Sections.SETUP + "." + ConfigurationLabels.Setup.CONSOLE_LOG, "DEBUG");

    }


//
//    @Test
//    public void missingStrategyTest() throws TaijituException, QueryUtilsException, SQLException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//        String tableName = "test";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}, {"3", "'TRES'"}, {"4", "'CUATRO'"}, {"5", "'CINCO'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//        String tableName2 = "test2";
//        TestUtils.createTable(conn, tableName2, columnDefs, columnValues);
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//        commonPropertiesSetup(testProperties);
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY,VALUE", "select * from test", "select * from test2", "test"));
//
//        testProperties.put("setup.strategy", "missing");
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(0, comparisonResults.length);
//    }
//
//
//    /**
//     * Test for databases with equalsFields content.
//     */
//    @Test
//    public void equalsTest() throws TaijituException, QueryUtilsException, SQLException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "test";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}, {"3", "'TRES'"}, {"4", "'CUATRO'"}, {"5", "'CINCO'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//        String tableName2 = "test2";
//        TestUtils.createTable(conn, tableName2, columnDefs, columnValues);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY,VALUE", "select * from test", "select * from test2", "test"));
//
//        // Disable plugins, 'cause we need to write nothing.
//        commonPropertiesSetup(testProperties);
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//
//        assertEquals(0, comparisonResult.getDifferent().size());
//        assertEquals(0, comparisonResult.getTargetOnly().size());
//        assertEquals(0, comparisonResult.getSourceOnly().size());
//    }
//
//
//    /**
//     * Test for databases elements not present in source.
//     */
//    @Test
//    public void missingInSourceTest() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[][] columnValues2 = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY,VALUE", "select * from source", "select * from target", "test"));
//
//        // Disable plugins, 'cause we need to write nothing.
//        commonPropertiesSetup(testProperties);
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//
//        assertEquals(0, comparisonResult.getDifferent().size());
//        assertEquals(1, comparisonResult.getTargetOnly().size());
//        assertEquals(0, comparisonResult.getSourceOnly().size());
//    }
//
//    /**
//     * Test for databases elements not present in target.
//     */
//    @Test
//    public void missingInTargetTest() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[][] columnValues2 = {{"1", "'UNO'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY,VALUE", "select * from source", "select * from target", "test"));
//
//        // Disable plugins, 'cause we need to write nothing.
//        commonPropertiesSetup(testProperties);
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//
//        assertEquals(0, comparisonResult.getDifferent().size());
//        assertEquals(0, comparisonResult.getTargetOnly().size());
//        assertEquals(1, comparisonResult.getSourceOnly().size());
//    }
//
//    /**
//     * Test for databases elements with different contents.
//     */
//    @Test
//    public void differenceTest() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[][] columnValues2 = {{"1", "'UNO'"}, {"2", "'TWO'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY", "select * from source", "select * from target", "test"));
//
//        // Disable plugins, 'cause we need to write nothing.
//        commonPropertiesSetup(testProperties);
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//
//        assertEquals(1, comparisonResult.getDifferent().size());
//        assertEquals(0, comparisonResult.getTargetOnly().size());
//        assertEquals(0, comparisonResult.getSourceOnly().size());
//    }
//
//    /**
//     * Test for databases elements not present in target.
//     */
//    @Test
//    public void duplicatedKeyTest() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'UNO'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues2 = {{"1", "'UNO'"}};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "VALUE", "select * from source", "select * from target", "test"));
//
//        // Disable plugins, 'cause we need to write nothing.
//        commonPropertiesSetup(testProperties);
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//
//        assertEquals(0, comparisonResult.getTargetOnly().size());
//        assertTrue("Should be at least 1 record present only in source.", comparisonResult.getSourceOnly().size() < 2);
//
//    }
//
//    /**
//     * Test for default values when fields parameters are not set. KEY, COMPARE
//     */
//    @Test
//    public void testFieldsFallback() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[][] columnValues2 = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//        commonPropertiesSetup(testProperties);
//        testProperties.putAll(databaseProps);
//
//        testProperties.put("comparison.test.fields", "KEY, VALUE");
//        testProperties.put("comparison.test.source.query", "select * from source");
//        testProperties.put("comparison.test.target.query", "select * from target");
//        testProperties.put("comparison.test.database", "test");
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResults[0].getStatus());
//
//    }
//
//    /**
//     * Test for fields to be detected automatically based on RecordSet Meta
//     */
//    @Test
//    public void testFieldsFromMeta() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//        String tableName = "source";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "target";
//        String[][] columnValues2 = {{"1", "'UNO'"}, {"2", "'DOS'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//        commonPropertiesSetup(testProperties);
//
//        testProperties.putAll(databaseProps);
//
//        testProperties.put("comparison.test.source.query", "select * from source");
//        testProperties.put("comparison.test.target.query", "select * from target");
//        testProperties.put("comparison.test.database", "test");
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//    }
//
//    @Test
//    public void testParameters() throws SQLException, TaijituException, QueryUtilsException {
//
//        // Create the tables and test data
//        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps);
//
//
//        String tableName = "test";
//        String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}, {"3", "'TRES'"}, {"4", "'CUATRO'"}, {"5", "'CINCO'"}};
//        TestUtils.createTable(conn, tableName, columnDefs, columnValues);
//
//        String tableName2 = "test2";
//        String[][] columnValues2 = {{"1", "'UNO'"}, {"3", "'DOS'"}, {"4", "'CUATRO'"}, {"5", "'CINCO'"}};
//        String[] columnDefs2 = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
//        TestUtils.createTable(conn, tableName2, columnDefs2, columnValues2);
//
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//        commonPropertiesSetup(testProperties);
//
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test", "KEY, VALUE", "KEY, VALUE", "KEY", "select * from test where key > {localParam}", "select * from test2 where key > {globalParam}", "test"));
//
//        // Parameters
//        //1.- Parameters in global section
//        testProperties.put(databaseProps.joinSections(Sections.SETUP, Comparison.PARAMETERS, "globalParam"), "2");
//        //2.- Parameters in comparison
//        testProperties.put(databaseProps.joinSections(Sections.COMPARISON, "test", Comparison.PARAMETERS, "localParam"), "2");
//
//        // Disable plugins, 'cause we need to write nothing.
//        testProperties.put(databaseProps.joinSections(Sections.SETUP, Setup.PLUGINS), "");
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//        assertEquals(1, comparisonResult.getDifferent().size());
//        assertTrue(comparisonResult.getTargetOnly().isEmpty());
//        assertTrue(comparisonResult.getSourceOnly().isEmpty());
//    }
//
//    @Test
//    public void pluginsTest() throws QueryUtilsException, TaijituException, SQLException {
//        // Create the tables and test data
//        final Properties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
//        Connection conn = getConnection(DB_NAME, databaseProps); // This generate the DB in H2
//
//        String[] commonSchema = {
//                "key INTEGER(5) PRIMARY KEY",
//                "char CHARACTER(1)",
//                "string VARCHAR(254)",
//                "bool BOOLEAN",
//                "small SMALLINT",// 	Integer numerical (no decimal). Precision 5
//                "medium INTEGER",// 	Integer numerical (no decimal). Precision 10
//                "big BIGINT",// 	Integer numerical (no decimal). Precision 19
//                "dec DECIMAL(5,3)",// 	Exact numerical, precision p, scale s. Example: decimal(5,2) is a number that has 3 digits before the decimal and 2 digits after the decimal
//                "num NUMERIC(5,3)",// 	Exact numerical, precision p, scale s. (Same as DECIMAL)
//                "flop FLOAT(2)",// 	Approximate numerical, mantissa precision p. A floating number in base 10 exponential notation. The size argument for this type consists of a single number specifying the minimum precision
//                "rea REAL",// 	Approximate numerical, mantissa precision 7
//                "flo FLOAT",// 	Approximate numerical, mantissa precision 16
//                "dou DOUBLE PRECISION",// 	Approximate numerical, mantissa precision 16
//                "dat DATE",// 	Stores year, month, and day values
//                "tim TIME",// 	Stores hour, minute, and second values
//                "timest TIMESTAMP"// 	Stores year, month, day, hour, minute, and second values
//        };
//
//        String tableName = "test";
//        String[][] commonValues = {{"1", "'A'", "'STRING'", "true", "10000", "1000000000", "1000000000000000000", "23.456", "89.102", "34.56", "78.9012345", "67.8901234567890123", "45.6789012345678901", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP"},
//                {"2", "'B'", "'STRING'", "true", "10000", "1000000000", "1000000000000000000", "23.456", "89.102", "34.56", "78.9012345", "67.8901234567890123", "45.6789012345678901", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP"}};
//        TestUtils.createTable(conn, tableName, commonSchema, commonValues);
//
//        String tableName2 = "test2";
//        TestUtils.createTable(conn, tableName2, commonSchema, commonValues);
//        conn.close();
//
//        HProperties testProperties = new HProperties();
//        //Databases
//        testProperties.putAll(databaseProps);
//        // Comparisons
//        testProperties.putAll(makeComparisonProps("test1", null, null, "KEY", "select * from test", "select * from test", "test"));
//        testProperties.putAll(makeComparisonProps("test2", null, null, "KEY", "select * from test", "select * from test2", "test"));
//
//        commonPropertiesSetup(testProperties);
//        testProperties.put("setup.scanClasspath", "true");
//        testProperties.put("setup.plugins", "csv,xls,timeLog");
//
//        final SimpleComparisonResult[] comparisonResults = new Taijitu().compare(testProperties.getDelegate());
//        assertEquals(2, comparisonResults.length);
//        final SimpleComparisonResult firstResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, firstResult.getStatus());
//        final SimpleComparisonResult secondResult = comparisonResults[1];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, secondResult.getStatus());
//
//        assertTrue("Output csv file exists and can be deleted", (new File("test2.difference.csv")).delete());
//        assertTrue("Output xls file exists and can be deleted", (new File("test2.difference.xls")).delete());
//
//    }


    private Properties makeComparisonProps(String name, String key, String sourceQuery, String targetQuery, String database) {
        Properties result = new Properties();
        if (key != null)
            result.setProperty(COMPARISON + "." + name + "." + KEY, key);
        if (sourceQuery != null)
            result.setProperty(COMPARISON + "." + name + "." + SOURCE + "." + STATEMENT, sourceQuery);
        if (targetQuery != null)
            result.setProperty(COMPARISON + "." + name + "." + TARGET + "." + STATEMENT, targetQuery);
        if (database != null) result.setProperty(COMPARISON + "." + name + "." + DATABASE_REF, database);
        return result;
    }

}