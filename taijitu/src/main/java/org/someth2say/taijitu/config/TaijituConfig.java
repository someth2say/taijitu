package org.someth2say.taijitu.config;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.properties.HPropertiesException;
import org.someth2say.taijitu.query.properties.HPropertiesHelper;
import org.someth2say.taijitu.strategy.mapping.ParallelQueryingMappingStrategy;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import static org.someth2say.taijitu.config.ConfigurationLabels.*;

public final class TaijituConfig {

    private static final String DEFAULT_FILE_LOG_LEVEL = "OFF";
    private static final String DEFAULT_CONSOLE_LOG_LEVEL = "INFO";
    private static final String DEFAULT_OUTPUT_FOLDER = ".";
    private static final double DEFAULT_PRECISION_THRESHOLD = 0d;
    private static final int DEFAULT_FETCHSIZE = 1024;
    private static final int DEFAULT_THREADS = 1;
    private static final String DEFAULT_STRATEGY_NAME = ParallelQueryingMappingStrategy.NAME;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    private static final Logger logger = Logger.getLogger(TaijituConfig.class);
    private static final String DEFAULT_PLUGINS = TimeLoggingPlugin.NAME;
    private static HProperties config;


    private TaijituConfig() {
    }

    /**** PARAMETERS *********************************************************************/

    public static Properties getAllParameters(final String testName) {
        final Properties result = new Properties();

        result.putAll(config.getPropertiesByPrefix(Sections.SETUP, Comparison.PARAMETERS)); // setup.parameters.XXX=YYY
        //TODO: Hierarchical subProperties
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, Comparison.PARAMETERS)); //comparison.parameters.XXX=YYY
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, testName, Comparison.PARAMETERS)); //comparison.N.parameters.XXX=YYY
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, testName, Sections.SETUP, Comparison.PARAMETERS)); //comparison.N.setup.parameters.XXX=YYY

        return result;
    }

    public static String getParameter(final String testName, final String paramName) {
        return getTestSetupWithFailbackToGlobalSetup(config.joinSections(Comparison.PARAMETERS, paramName), testName, null);
    }

    public static String getTestSetupWithFailbackToGlobalSetup(final String param, final String testName, final String defaultValue) {
        String result = config.getHierarchycalProperty(param, Sections.COMPARISON, testName, Sections.SETUP);
        if (result == null) {
            result = config.getProperty(param, Sections.SETUP);
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /**** COMPARISON *********************************************************************/

    public static String getStrategyName(final String testName) {
        return getTestSetupWithFailbackToGlobalSetup(Comparison.STRATEGY, testName, DEFAULT_STRATEGY_NAME);
    }

    public static String[] getComparisonPlugins(final String testName) {
        return StringUtil.splitAndTrim(getTestSetupWithFailbackToGlobalSetup(Setup.PLUGINS, testName, DEFAULT_PLUGINS));
    }

    public static String[] getAllPlugins() {
        return StringUtil.splitAndTrim(getSetup(Setup.PLUGINS, DEFAULT_PLUGINS));

    }

    public static Set<String> getComparisonNames() {
        final HProperties comparisons = config.getSubPropertiesByPrefix(Sections.COMPARISON);
        return comparisons.getPropertiesRoots();
    }

    public static Integer getFetchSize(final String testName) {
        try {
            return Integer.valueOf(getTestSetupWithFailbackToGlobalSetup(Setup.FETCH_SIZE, testName, Integer.toString(DEFAULT_FETCHSIZE)));
        } catch (final NumberFormatException e) {
            logger.warn("FetchSize can't be parsed. Please, review configuration file.");
        }
        return DEFAULT_FETCHSIZE;
    }

    public static String getHeader(final String testName) {
        return config.getHierarchycalProperty(Comparison.FIELDS, Sections.COMPARISON, testName, Comparison.FIELDS);
    }

    public static String getKeyFields(final String testName) {
        return getFieldsParameter(testName, Comparison.Fields.KEY);

    }

    public static String getCompareFields(final String testName) {
        return getFieldsParameter(testName, Comparison.Fields.COMPARE);
    }

    private static String getFieldsParameter(String testName, String fieldParameter) {
        final String fieldValue = config.getHierarchycalProperty(fieldParameter, Sections.COMPARISON, testName, Comparison.FIELDS);
        if (fieldValue == null) {
            return getHeader(testName);
        }
        return fieldValue;
    }


    public static String getSourceQuery(final String testName) {
        return config.getHierarchycalProperty(Comparison.QUERY, Sections.COMPARISON, testName, Comparison.SOURCE);
    }

    public static String getTargetQuery(final String testName) {
        return config.getHierarchycalProperty(Comparison.QUERY, Sections.COMPARISON, testName, Comparison.TARGET);
    }

    public static String getSourceQueryName(final String testName) {
        return config.getHierarchycalProperty(Comparison.SOURCE, Sections.COMPARISON, testName);
    }

    public static String getTargetQueryName(final String testName) {
        return config.getHierarchycalProperty(Comparison.TARGET, Sections.COMPARISON, testName);
    }


    /**** DATABASE *********************************************************************/

    public static String getDatabaseName(String testName, String sourceOrTarget) {
        String databaseRef = config.getHierarchycalProperty(Comparison.DATABASE_REF, Sections.COMPARISON, testName, sourceOrTarget);
        if (databaseRef == null) {
            // If only one, use it
            final Set<String> databaseNames = getAllDatabaseNames();
            if (databaseNames.size() == 1) {
                databaseRef = databaseNames.iterator().next();
                logger.info("No database defined for " + testName + ". Using only one defined: " + databaseRef);
            }
        }
        return databaseRef;
    }

    public static Set<String> getAllDatabaseNames() {
        final HProperties databaseProperties = config.getPropertiesByPrefix(Sections.DATABASE);
        return databaseProperties.getPropertiesRoots();
    }


    /**** SETUP *********************************************************************/

    public static Properties getAllSetup(final String testName) {
        final Properties result = new Properties();
        result.putAll(config.getPropertiesByPrefix(Sections.SETUP));
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, Sections.SETUP));
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, testName, Sections.SETUP));
        return result;
    }

    public static String getSetup(final String property, final String def) {
        final String value = config.getProperty(property, Sections.SETUP);
        return value == null ? def : value;
    }

    public static int getThreads() {
        final String threadsStr = getSetup(Setup.THREADS, null);
        if (threadsStr != null) {
            try {
                return Integer.valueOf(threadsStr);
            } catch (final NumberFormatException e) {
                logger.warn("Threads can't be parsed. Please, review configuration file.");
            }
        }
        return DEFAULT_THREADS;
    }

    public static String getConsoleLog() {
        return getSetup(Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    public static String getFileLog() {
        return getSetup(Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    public static String getOutputFolder() {
        return getSetup(Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    public static double getPrecisionThreshold(final String testName) {
        final String ptString = config.getHierarchycalProperty(config.joinSections(Sections.SETUP, Setup.PRECISION_THRESHOLD), null, Sections.COMPARISON, testName);
        if (ptString == null) {
            return 0d;
        }
        try {
            return Double.parseDouble(ptString);
        } catch (final NumberFormatException e) {
            logger.error("Precision threshold can't be parsed. Using " + DEFAULT_PRECISION_THRESHOLD + " threshold", e);
        }
        return DEFAULT_PRECISION_THRESHOLD;
    }

    public static boolean isQueryOptimization(final String testName) {
        return isEntryBySection(Setup.QUERY_OPTIMIZATION, testName, Sections.SETUP);
    }

    /**** UTILITIES *********************************************************************/

    public static File getOutputFolderFile() {
        return new File(getOutputFolder());
    }

    public static boolean isEntryBySection(final String paramName, final String testName, final String section) {
        final String sectionAndParam = config.joinSections(section, paramName);

        String strValue = config.getHierarchycalProperty(sectionAndParam, null, Sections.COMPARISON, testName);
        if (strValue != null) {
            return Boolean.valueOf(strValue);
        }
        return false;
    }

    public static Date parseDate(String paramName, String paramValue) throws TaijituException {
        Date result = null;
        if (paramValue != null) {
            try {
                final long millis = DATE_TIME_FORMATTER.parseMillis(paramValue);
                result = new Date(millis);
            } catch (final IllegalArgumentException e) {
                throw new TaijituException("Unable to parse date " + paramName, e);
            }
        }
        return result;
    }

    /**
     * Tries to load configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to load
     */
    public static HProperties setConfigProperties(final String configFile) throws TaijituException {
        HProperties properties = loadProperties(configFile);
        setProperties(properties);
        return properties;
    }


    private static HProperties loadProperties(String configFile) throws TaijituException {
        try (final InputStream configStream = new FileInputStream(configFile)) {
            validateStream(configFile, configStream);
            return HPropertiesHelper.load(configStream);
        } catch (HPropertiesException | IOException e) {
            throw new TaijituException("Unable to load configuration from file: " + configFile, e);
        }
    }

    private static void validateStream(String configFile, InputStream configStream) throws TaijituException {
        if (configStream == null) {
            throw new TaijituException("Can't find configuration file: " + configFile + "(file exists?)");
        }
    }

    public static HProperties getProperties() {
        return config;
    }

    public static void setProperties(HProperties properties) {
        config = properties;
    }

    public static Boolean isUseScanClassPath() {
        return Boolean.valueOf(getSetup(Setup.SCAN_CLASSPATH, "false"));
    }

    public static HProperties getDatabaseProperties() {
        return getProperties().getPropertiesByPrefix(Sections.DATABASE);
    }
}