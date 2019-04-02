package org.someth2say.taijitu.cli;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.TaijituConfig;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.cli.source.query.ConnectionManager;
import org.someth2say.taijitu.cli.source.query.QuerySource;
import org.someth2say.taijitu.equality.explain.Difference;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Jordi Sola
 */
public class CLITest_DB {

    private static final String DB_NAME = "test";
    private static final String DB_USER = "user";
    private static final String DB_PWD = "pwd";

    @After
    public void dropTables() throws SQLException {
        Connection conn = ConnectionManager.getConnection(TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD));
        TestUtils.dropRegisteredTables(conn);
    }

    @Test
    public void basicDBTest() throws TaijituCliException, SQLException {
        // Create the tables and test data
        final Properties databaseProps = buildDbSampleData();

        final ITaijituCfg configuration = TaijituConfig.fromYamlFile("test_db.yaml");

        final List<Stream<Difference>> comparisonResults = TaijituCli.compare(configuration);

        Assert.assertEquals(configuration.getComparisons().size(), comparisonResults.size());
        final List<Difference> firstResult = comparisonResults.get(0).collect(Collectors.toList());
        System.out.println(firstResult);
        Assert.assertEquals(0, firstResult.size());
        final List<Difference> secondResult = comparisonResults.get(1).collect(Collectors.toList());
        System.out.println(secondResult);
        Assert.assertEquals(1, secondResult.size());
    }

    private Properties buildDbSampleData() throws SQLException {
        final Properties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        Connection conn = ConnectionManager.getConnection(databaseProps); // This generate the DB in H2

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
        commonValues[1][9] = "34.57"; // This one should be absorbed by threshold
        TestUtils.createTable(conn, "test2", commonSchema, commonValues);

        conn.close();
        return databaseProps;
    }


    private void dumpConfig(ImmutableHierarchicalConfiguration configuration) throws ConfigurationException {
        try {
            YAMLConfiguration yamlConfiguration = new BasicConfigurationBuilder<>(YAMLConfiguration.class).getConfiguration();
            ConfigurationUtils.copy(configuration, yamlConfiguration);
            yamlConfiguration.write(new OutputStreamWriter(System.out));
        } catch (IOException e) {
            // Do nothing, this is just for test.
        }

    }

    //
//    @Test
//    public void missingStrategyTest() throws TaijituCliException, QueryUtilsException, SQLException {
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
//        testProperties.put("setup.stream", "missing");
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
//        assertEquals(0, comparisonResults.length);
//    }
//
//
//    /**
//     * Test for databases with equalsFields content.
//     */
//    @Test
//    public void equalsTest() throws TaijituCliException, QueryUtilsException, SQLException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//    public void missingInSourceTest() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//    public void missingInTargetTest() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//    public void differenceTest() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//    public void duplicatedKeyTest() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//    public void testFieldsFallback() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResults[0].getStatus());
//
//    }
//
//    /**
//     * Test for fields to be detected automatically based on RecordSet Meta
//     */
//    @Test
//    public void testFieldsFromMeta() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//    }
//
//    @Test
//    public void testParameters() throws SQLException, TaijituCliException, QueryUtilsException {
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
//        testProperties.put(databaseProps.joinSections(Sections.SETUP, ComparisonCfg.PARAMETERS, "globalParam"), "2");
//        //2.- Parameters in comparison
//        testProperties.put(databaseProps.joinSections(Sections.COMPARISON, "test", ComparisonCfg.PARAMETERS, "localParam"), "2");
//
//        // Disable plugins, 'cause we need to write nothing.
//        testProperties.put(databaseProps.joinSections(Sections.SETUP, Setup.PLUGINS), "");
//
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
//        assertEquals(1, comparisonResults.length);
//        final SimpleComparisonResult comparisonResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, comparisonResult.getStatus());
//        assertEquals(1, comparisonResult.getDifferent().size());
//        assertTrue(comparisonResult.getTargetOnly().isEmpty());
//        assertTrue(comparisonResult.getSourceOnly().isEmpty());
//    }
//
//    @Test
//    public void pluginsTest() throws QueryUtilsException, TaijituCliException, SQLException {
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
//        assertEquals(2, comparisonResults.length);
//        final SimpleComparisonResult firstResult = comparisonResults[0];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, firstResult.getStatus());
//        final SimpleComparisonResult secondResult = comparisonResults[1];
//        assertEquals(SimpleComparisonResult.ComparisonResultStatus.SUCCESS, secondResult.getStatus());
//
//        assertTrue("Output csv file exists and can be deleted", (new File("test2.differenceOrNull.csv")).delete());
//        assertTrue("Output xls file exists and can be deleted", (new File("test2.differenceOrNull.xls")).delete());
//
//    }


    private Properties makeComparisonProps(String name, String compare, String keys, String sort, Properties sourceSource, Properties targetSource, Properties database) {
        String comparisonPrefix = ConfigurationLabels.COMPARISON + "." + name + ".";
        Properties result = new Properties();
        if (compare != null)
            result.setProperty(comparisonPrefix + ConfigurationLabels.COMPARE, compare);

        if (keys != null)
            result.setProperty(comparisonPrefix + ConfigurationLabels.KEYS, keys);

        if (sort != null)
            result.setProperty(comparisonPrefix + ConfigurationLabels.SORT, sort);

        if (sourceSource != null) {
            putAll(result, sourceSource, comparisonPrefix + ConfigurationLabels.SOURCES + ".source.");
        }

        if (targetSource != null) {
            putAll(result, targetSource, comparisonPrefix + ConfigurationLabels.SOURCES + ".target.");
        }

        if (database != null) {
            result.setProperty(comparisonPrefix + ConfigurationLabels.SOURCE_BUILD_PROPERTIES, linearizeProperties(database));
        }

        return result;
    }


    private Properties makeQuerySourceProps(String query, Properties databaseProperties, String mapperName) {
        Properties result = new Properties();
        result.put(ConfigurationLabels.SOURCE_TYPE, QuerySource.class.getSimpleName());

        Properties fetchProperties = new Properties();
        fetchProperties.put(ConfigurationLabels.STATEMENT, query);

        result.put(ConfigurationLabels.SOURCE_FETCH_PROPERTIES, linearizeProperties(fetchProperties));

        if (databaseProperties != null) {
            result.put(ConfigurationLabels.SOURCE_BUILD_PROPERTIES, linearizeProperties(databaseProperties));
        }

        if (mapperName != null) {
            result.put(ConfigurationLabels.MAPPER_TYPE, mapperName);
        }

        return result;
    }

    private String linearizeProperties(Properties properties) {
        return properties.entrySet().stream().map(entry -> entry.getKey().toString() + "=" + entry.getValue().toString()).collect(Collectors.joining(", "));
    }


    private static void putAll(final PropertiesConfiguration to, final Properties from, final String keyPrefix) {
        from.forEach((o, o2) -> to.addProperty(keyPrefix + o.toString(), o2));
    }

    private static void putAll(final Properties to, final Properties from, final String keyPrefix) {
        from.forEach((o, o2) -> to.put(keyPrefix + o.toString(), o2));
    }


}

