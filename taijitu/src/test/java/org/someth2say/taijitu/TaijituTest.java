package org.someth2say.taijitu;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.compare.equality.composite.CompositeComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.value.*;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.ui.config.ConfigurationLabels;
import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.delegates.simple.*;
import org.someth2say.taijitu.ui.config.impl.TaijituCfg;
import org.someth2say.taijitu.ui.config.interfaces.*;
import org.someth2say.taijitu.ui.config.source.csv.CSVResourceSource;
import org.someth2say.taijitu.ui.config.source.mapper.CSVTupleMapper;
import org.someth2say.taijitu.ui.config.source.mapper.ResultSetTupleMapper;
import org.someth2say.taijitu.ui.config.source.query.ConnectionManager;
import org.someth2say.taijitu.ui.config.source.query.QuerySource;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.someth2say.taijitu.ui.config.ConfigurationLabels.Comparison.*;
import static org.someth2say.taijitu.ui.config.ConfigurationLabels.Comparison.Fields.KEYS;
import static org.someth2say.taijitu.ui.config.ConfigurationLabels.Sections.COMPARISON;


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
    public static Collection<String> strategies() {
        return Arrays.asList(
                MappingStreamEquality.NAME, ComparableStreamEquality.NAME
//                ,MappingStreamEquality.NAME, ComparableStreamEquality.NAME
//                ,MappingStreamEquality.NAME, ComparableStreamEquality.NAME
//                ,MappingStreamEquality.NAME, ComparableStreamEquality.NAME
//                ,MappingStreamEquality.NAME, ComparableStreamEquality.NAME
//                ,MappingStreamEquality.NAME, ComparableStreamEquality.NAME
        );
    }

    @After
    public void dropTables() throws SQLException, ConfigurationException {
        Connection conn = ConnectionManager.getConnection(TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD));
        TestUtils.dropRegisteredTables(conn);
    }

    @Test
    public void basicDBTest() throws TaijituException, SQLException, ConfigurationException, InterruptedException {
        // Create the tables and test data
        final Properties databaseProps = buildDbSampleData();

        final ITaijituCfg configuration = getTaijituConfig(databaseProps);

        final ComparisonResult[] comparisonResults = Taijitu.compare(configuration);

        assertEquals(2, comparisonResults.length);
        final ComparisonResult firstResult = comparisonResults[0];
        System.out.println(firstResult.getMismatches());
        assertEquals(0, firstResult.getMismatches().size());
        final ComparisonResult secondResult = comparisonResults[1];
        System.out.println(secondResult.getMismatches());
        assertEquals(1, secondResult.getMismatches().size());
    }

    @Test
    public void apacheDBTest() throws TaijituException, SQLException, ConfigurationException, InterruptedException {
        // Create the tables and test data
        final Properties sourceBuildProperties = buildDbSampleData();

        final ImmutableHierarchicalConfiguration configuration = getApacheConfiguration(sourceBuildProperties);

        final ComparisonResult[] comparisonResults = Taijitu.compare(configuration);

        assertEquals(2, comparisonResults.length);
        final ComparisonResult firstResult = comparisonResults[0];
        Collection<ComparisonResult.Mismatch> firstResultMismatches = firstResult.getMismatches();
        System.out.println(firstResultMismatches);
        assertEquals(0, firstResultMismatches.size());
        final ComparisonResult secondResult = comparisonResults[1];
        Collection<ComparisonResult.Mismatch> secondResultMismatches = secondResult.getMismatches();
        System.out.println(secondResultMismatches);
        assertEquals(1, secondResultMismatches.size());

        //TODO: Define exactly the expected difference!
//        Collection<Pair<ComparisonResult.QueryAndTuple, ComparisonResult.QueryAndTuple>> different = secondResult.getDifferent();
//        Pair<ComparisonResult.QueryAndTuple, ComparisonResult.QueryAndTuple> dif = different.iterator().next();
//        dif.equals(new ImmutablePair<>(new ComparisonResult.QueryAndTuple(sourceCfg,sourceTuple), new ComparisonResult.QueryAndTuple(targetCfg,targetTuple)));


    }

    @Test
    public void CSVTest() throws TaijituException, SQLException, ConfigurationException, InterruptedException {
        // Create the tables and test data
        final TaijituCfg configuration = getCSVConfiguration();

        final ComparisonResult[] comparisonResults = Taijitu.compare(configuration);

        assertEquals(1, comparisonResults.length);
        final ComparisonResult firstResult = comparisonResults[0];
        assertEquals(0, firstResult.getMismatches().size());
        System.out.println(firstResult.getMismatches());

    }

    private TaijituCfg getCSVConfiguration() {
        BasicTaijituCfg basicTaijituCfg = new BasicTaijituCfg("");

        // Comparisons

        Properties s1buildProperties = new Properties();
        //URL Scheme
        s1buildProperties.setProperty(ConfigurationLabels.Comparison.RESOURCE, "http://samplecsvs.s3.amazonaws.com/Sacramentorealestatetransactions.csv");

        Properties s2buildProperties = new Properties();
        // No scheme: File source (should be in classpath)
        s2buildProperties.setProperty(ConfigurationLabels.Comparison.RESOURCE, "/csv/Sacramentorealestatetransactions.csv");
        // File scheme (must be absolute)
//        s2buildProperties.setProperty(ConfigurationLabels.Comparison.RESOUCE, "file:///"+ ClassLoader.getSystemResource(".").getPath() +"/csv/Sacramentorealestatetransactions.csv");

        // We don't actually need a mapper here, we can compare directly the strings provided by the source.
        BasicSourceCfg sourceSrc = new BasicSourceCfg("source", CSVResourceSource.NAME, null, s1buildProperties, null);//CSVTupleMapper.NAME);
        BasicSourceCfg targetSrc = new BasicSourceCfg("target", CSVResourceSource.NAME, null, s2buildProperties, CSVTupleMapper.NAME);

        BasicComparisonCfg comp1 = new BasicComparisonCfg("csv", Arrays.asList("street", "price", "latitude", "longitude"), Arrays.asList(sourceSrc, targetSrc));
        basicTaijituCfg.setComparisons(Collections.singletonList(comp1));

        //Strategy
        basicTaijituCfg.setStrategyConfig(new BasicStrategyCfg(strategyName));

        // Equality
        BasicEqualityCfg stringEq = new BasicEqualityCfg(StringCaseInsensitive.class.getSimpleName(), String.class.getName(), null);
        BasicEqualityCfg numberEq = new BasicEqualityCfg(NumberThreshold.class.getSimpleName(), Number.class.getName(), null, "2");
        IEqualityCfg timestampEq = new BasicEqualityCfg(DateThreshold.class.getSimpleName(), Date.class.getName(), null, "100");
        basicTaijituCfg.setEqualityConfigs(Arrays.asList(stringEq, numberEq, timestampEq));

        return new TaijituCfg(basicTaijituCfg);
    }


    private ITaijituCfg getTaijituConfig(Properties dbProperties) {
        BasicTaijituCfg basicTaijituCfg = new BasicTaijituCfg("");

        // Databases
        basicTaijituCfg.setBuildProperties(dbProperties);
        // Comparisons

        Properties s1fetchProperties = new Properties();
        s1fetchProperties.setProperty(ConfigurationLabels.Comparison.STATEMENT, "select * from test");

        Properties s2fetchProperties = new Properties();
        s2fetchProperties.setProperty(ConfigurationLabels.Comparison.STATEMENT, "select * from test2");

        BasicSourceCfg sourceSrc = new BasicSourceCfg("source", QuerySource.NAME, s1fetchProperties, null, ResultSetTupleMapper.NAME);
        BasicSourceCfg source2Src = new BasicSourceCfg("source2", QuerySource.NAME, s1fetchProperties, null, ResultSetTupleMapper.NAME);
        BasicSourceCfg targetSrc = new BasicSourceCfg("target", QuerySource.NAME, s2fetchProperties, null, ResultSetTupleMapper.NAME);

        BasicComparisonCfg comp1 = new BasicComparisonCfg("test1", Collections.singletonList("KEY"), Arrays.asList(sourceSrc, source2Src));
        BasicComparisonCfg comp2 = new BasicComparisonCfg("test2", Collections.singletonList("KEY"), Arrays.asList(sourceSrc, targetSrc));
        basicTaijituCfg.setComparisons(Arrays.asList(comp1, comp2));

        //Strategy
        basicTaijituCfg.setStrategyConfig(new BasicStrategyCfg(strategyName));

        // Equality
        BasicEqualityCfg stringEq = new BasicEqualityCfg(StringCaseInsensitive.class.getSimpleName(), String.class.getName(), null);
        BasicEqualityCfg numberEq = new BasicEqualityCfg(NumberThreshold.class.getSimpleName(), Number.class.getName(), null, "2");
        IEqualityCfg timestampEq = new BasicEqualityCfg(DateThreshold.class.getSimpleName(), Date.class.getName(), null, "100");
        basicTaijituCfg.setEqualityConfigs(Arrays.asList(stringEq, numberEq, timestampEq));

        return new TaijituCfg(basicTaijituCfg);
    }

    private ImmutableHierarchicalConfiguration getApacheConfiguration(Properties sourceBuildProperties) throws ConfigurationException {

        final PropertiesConfiguration properties = new BasicConfigurationBuilder<>(PropertiesConfiguration.class).getConfiguration();

        properties.setListDelimiterHandler(new DefaultListDelimiterHandler(DefaultConfig.DEFAULT_LIST_DELIMITER));
        //Databases
        //putAll(properties, sourceBuildProperties, DATABASE + ".");

        // Comparisons
        //TODO: ResultSet are transient objects, so can not be used for mapping comparison! Mapper is a must. Should we detect it?
        Properties sourceProps1 = makeQuerySourceProps("select * from test", sourceBuildProperties, ResultSetTupleMapper.NAME);
        Properties sourceProps2 = makeQuerySourceProps("select * from test2", sourceBuildProperties, ResultSetTupleMapper.NAME);
        putAll(properties, makeComparisonProps("test1", "KEY", sourceProps1, sourceProps1, null), "");
        putAll(properties, makeComparisonProps("test2", "KEY", sourceProps1, sourceProps2, null), "");

        // Disable plugins, 'cause we need to write nothing.
        properties.setProperty(ConfigurationLabels.Comparison.STRATEGY, strategyName);

        //Add comparators
        //Case insensitive strings
        properties.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + StringCaseInsensitive.class.getSimpleName() + "." + ConfigurationLabels.Comparison.FIELD_CLASS, String.class.getName());
        //Decimal places for Numbers
        properties.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + NumberThreshold.class.getSimpleName() + "." + ConfigurationLabels.Comparison.FIELD_CLASS, Number.class.getName());
        properties.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + NumberThreshold.class.getSimpleName() + "." + ConfigurationLabels.Comparison.EQUALITY_PARAMS, "2");
        //Threshold 100ms for Timestamps.
        properties.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + DateThreshold.class.getSimpleName() + "." + ConfigurationLabels.Comparison.FIELD_CLASS, Date.class.getName());
        properties.setProperty(ConfigurationLabels.Comparison.EQUALITY + "." + DateThreshold.class.getSimpleName() + "." + ConfigurationLabels.Comparison.EQUALITY_PARAMS, "100");

        final ImmutableHierarchicalConfiguration configuration = ConfigurationUtils.unmodifiableConfiguration(ConfigurationUtils.convertToHierarchical(properties));

        dumpConfig(configuration);


        return configuration;
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

    @Test
    public void testCompositeEqualityBuilder() {
        List<ExtractorAndEquality<TestClass, ?>> equalityEaEs = new ArrayList<>();
        equalityEaEs.add(new ExtractorAndEquality<>(TestClass::getOne, new StringCaseInsensitive()));
        equalityEaEs.add(new ExtractorAndEquality<>(TestClass::getTwo, new StringCaseInsensitive()));
        CompositeEquality<TestClass> equality = new CompositeEquality<>(equalityEaEs);

        List<ExtractorAndComparableCategorizerEquality<TestClass, ?>> comparerEaEs = new ArrayList<>();
        Function<TestClass, Integer> getThree = TestClass::getThree;
        comparerEaEs.add(new ExtractorAndComparableCategorizerEquality<TestClass,Integer>(getThree, new ObjectToString()));
        CompositeComparableCategorizerEquality<TestClass> comparer = new CompositeComparableCategorizerEquality<>(comparerEaEs);
        Stream<TestClass> stream1 = Stream.of(
                new TestClass("aaa", "aaa", 1),
                new TestClass("bbb", "bbb", 1),
                new TestClass("ccc", "ccc", 2)
        );

        Stream<TestClass> stream2 = Stream.of(
                new TestClass("aAA", "aAa", 1),
                new TestClass("bbb", "bb", 1),
                new TestClass("ccc", "ccc", 3)
        );

        ComparableStreamEquality.compare(stream1, 1, stream2, 2, comparer, equality);
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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
//        final SimpleComparisonResult[] comparisonResults = new TaijituCfg().compare(testProperties.getDelegate());
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


    private Properties makeComparisonProps(String name, String keys, Properties sourceSource, Properties targetSource, Properties database) {
        String comparisonPrefix = COMPARISON + "." + name + ".";
        Properties result = new Properties();
        if (keys != null)
            result.setProperty(comparisonPrefix + KEYS, keys);

        if (sourceSource != null) {
            putAll(result, sourceSource, comparisonPrefix + SOURCES + ".source.");
        }

        if (targetSource != null) {
            putAll(result, targetSource, comparisonPrefix + SOURCES + ".target.");
        }

        if (database != null) {
            result.setProperty(comparisonPrefix + ConfigurationLabels.Comparison.SOURCE_BUILD_PROPERTIES, linearizeProperties(database));
        }

        return result;
    }


    private Properties makeQuerySourceProps(String query, Properties databaseProperties, String mapperName) {
        Properties result = new Properties();
        result.put(SOURCE_TYPE, QuerySource.NAME);

        Properties fetchProperties = new Properties();
        fetchProperties.put(STATEMENT, query);

        result.put(ConfigurationLabels.Comparison.SOURCE_FETCH_PROPERTIES, linearizeProperties(fetchProperties));

        if (databaseProperties != null) {
            result.put(ConfigurationLabels.Comparison.SOURCE_BUILD_PROPERTIES, linearizeProperties(databaseProperties));
        }

        if (mapperName != null) {
            result.put(ConfigurationLabels.Comparison.MAPPER_TYPE, mapperName);
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

class TestClass {
    private final String one;
    private final String two;
    private final Integer three;

    TestClass(String one, String two, Integer three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public String getOne() {
        return one;
    }

    public String getTwo() {
        return two;
    }

    public Integer getThree() {
        return three;
    }
}
