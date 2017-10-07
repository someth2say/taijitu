package org.someth2say.taijitu.config;

import static org.someth2say.taijitu.config.DefaultConfig.DATE_TIME_FORMATTER;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_CONSOLE_LOG_LEVEL;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FILE_LOG_LEVEL;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_OUTPUT_FOLDER;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_PRECISION_THRESHOLD;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_THREADS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Sections;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.properties.HPropertiesException;
import org.someth2say.taijitu.query.properties.HPropertiesHelper;

public final class TaijituConfigImpl implements TaijituConfig {

    private static final Logger logger = Logger.getLogger(TaijituConfigImpl.class);

    private HProperties config;

    private TaijituConfigImpl() {
    }

    public static TaijituConfig fromFile(String file) throws TaijituException {
        TaijituConfigImpl result = new TaijituConfigImpl();
        result.loadProperties(file);
        return result;
    }

    /**** PARAMETERS *********************************************************************/

    public Properties getAllParameters() {
        final Properties result = new Properties();
        result.putAll(config.getPropertiesByPrefix(Sections.SETUP, Comparison.PARAMETERS)); // setup.parameters.XXX=YYY
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, Comparison.PARAMETERS)); //comparison.parameters.XXX=YYY
        return result;
    }

    public String getParameter(String parameterName) {
        final String parameterKey = config.joinSections(Comparison.PARAMETERS, parameterName);
        String parameterValue = config.getHierarchycalProperty(parameterKey, Sections.COMPARISON, Comparison.PARAMETERS);
        if (parameterValue == null) {
            parameterValue = config.getHierarchycalProperty(parameterKey, Sections.SETUP, Comparison.PARAMETERS);
        }
        return parameterValue;

    }


    /**** COMPARISONS *********************************************************************/
    public Set<String> getComparisonNames() {
        final HProperties comparisons = config.getPropertiesByPrefix(Sections.COMPARISON);
        return comparisons.getPropertiesRoots();
    }

    public ComparisonConfig[] getComparisons() {
        Set<String> comparisonNames = getComparisonNames();
        ComparisonConfig[] result = new ComparisonConfig[comparisonNames.size()];
        int pos = 0;
        for (Iterator<String> iterator = comparisonNames.iterator(); iterator.hasNext(); pos++) {
            result[pos] = new ComparisonConfigImpl(this, iterator.next());
        }
        return result;

    }

    /**** DATABASE *********************************************************************/

    public DatabaseConfig getDatabaseConfig(final String databaseName) {
        return new DatabaseConfigImpl(this, databaseName);
    }


    @Override
    public DatabaseConfig[] getAllDatabaseConfigs() {
        Set<String> allDatabaseNames = getAllDatabaseNames();
        int pos = 0;
        DatabaseConfig[] result = new DatabaseConfig[allDatabaseNames.size()];
        for (Iterator<String> iterator = allDatabaseNames.iterator(); iterator.hasNext(); pos++) {
            result[pos] = new DatabaseConfigImpl(this, iterator.next());
        }
        return result;
    }


    private Set<String> getAllDatabaseNames() {
        final HProperties databaseProperties = config.getPropertiesByPrefix(Sections.DATABASE);
        return databaseProperties.getPropertiesRoots();
    }


    /**** SETUP *********************************************************************/

    /**
     * TaijituConfig does not define any entry itself, but delegates all entries to "SETUP" section.
     */
    public String getProperty(final String paramName, final String defaultValue) {
        return getSetup(paramName, defaultValue);
    }

    private String getSetup(final String property, final String def) {
        final String value = config.getProperty(property, Sections.SETUP);
        return value == null ? def : value;
    }

    /**
     * Number of threads Taijitu will raise for running comparisons.
     * Note that each comparison strategy may require different number of threads.
     */
    public int getThreads() {
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

    public String getConsoleLog() {
        return getSetup(Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    public String getFileLog() {
        return getSetup(Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    public String getOutputFolder() {
        return getSetup(Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    public double getPrecisionThreshold() {
        final String ptString = getSetup(Setup.PRECISION_THRESHOLD, null);
        if (ptString != null) {
            try {
                return Double.parseDouble(ptString);
            } catch (final NumberFormatException e) {
                logger.error("Precision threshold can't be parsed. Using " + DEFAULT_PRECISION_THRESHOLD + " threshold", e);
            }
        }
        return DEFAULT_PRECISION_THRESHOLD;
    }

    public int getFetchSize() {
        final String fsString = getSetup(Setup.FETCH_SIZE, null);
        if (fsString != null) {
            try {
                return Integer.parseInt(fsString);
            } catch (final NumberFormatException e) {
                logger.error("Fetch size can't be parsed. Using " + DEFAULT_FETCHSIZE + " as fetch size", e);
            }
        }
        return DEFAULT_FETCHSIZE;
    }

    /**** UTILITIES *********************************************************************/

    public File getOutputFolderFile() {
        return new File(getOutputFolder());
    }

    public static Date parseDate(String dateStr) throws TaijituException {
        Date result = null;
        if (dateStr != null) {
            try {
                result = new Date(DATE_TIME_FORMATTER.parseMillis(dateStr));
            } catch (final IllegalArgumentException e) {
                throw new TaijituException("Unable to parse date " + dateStr, e);
            }
        }
        return result;
    }

    /**
     * Tries to load configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to load
     */
    private void loadProperties(String configFile) throws TaijituException {
        try (final InputStream configStream = new FileInputStream(configFile)) {
            validateStream(configFile, configStream);
            config = HPropertiesHelper.load(configStream);
        } catch (HPropertiesException | IOException e) {
            throw new TaijituException("Unable to load configuration from file: " + configFile, e);
        }
    }

    private void validateStream(String configFile, InputStream configStream) throws TaijituException {
        if (configStream == null) {
            throw new TaijituException("Can't find configuration file: " + configFile + "(file exists?)");
        }
    }

    public HProperties getProperties() {
        return config;
    }

    public Boolean isUseScanClassPath() {
        return Boolean.valueOf(getSetup(Setup.SCAN_CLASSPATH, "false"));
    }

    @Override
    public ComparisonPluginConfig[] getAllPluginsConfig() {
        return new ComparisonPluginConfig[0];
    }

}
