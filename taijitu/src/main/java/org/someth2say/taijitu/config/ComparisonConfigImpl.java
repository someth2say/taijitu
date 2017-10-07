package org.someth2say.taijitu.config;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.config.ConfigurationLabels.*;
import org.someth2say.taijitu.query.properties.HProperties;

import static org.someth2say.taijitu.config.DefaultConfig.*;


public class ComparisonConfigImpl implements ComparisonConfig {
    private static final Logger logger = Logger.getLogger(ComparisonConfigImpl.class);
    private final TaijituConfigImpl taijituConfig;
    private final HProperties config;
    private final String name;

    public ComparisonConfigImpl(TaijituConfigImpl taijituConfig, final String testName) {
        this.taijituConfig = taijituConfig;
        this.name = testName;
        this.config = taijituConfig.getProperties();
    }

    public HProperties getConfig() {
        return this.config;
    }


    public String getName() {
        return name;
    }


    /**** PARAMETERS *********************************************************************/

    public Properties getAllParameters() {
        final Properties result = new Properties();
        result.putAll(taijituConfig.getAllParameters());
        result.putAll(config.getPropertiesByPrefix(Sections.COMPARISON, getName(), Comparison.PARAMETERS)); //comparison.comparisonName.parameters.XXX=YYY
        return result;
    }

    public String getParameter(String parameterName) {
        String parameterKey = config.joinSections(Comparison.PARAMETERS, parameterName);
        String parameterValue = config.getHierarchycalProperty(parameterKey, Sections.COMPARISON, getName(), Comparison.PARAMETERS);
        return parameterValue != null ? parameterValue : taijituConfig.getParameter(parameterName);
    }


    /**
     * PROPERTIES
     *************************************************************************/

    public String getProperty(final String paramName, final String defaultValue) {
        return getPropertyWithFailback(config.joinSections(Comparison.PARAMETERS, paramName), defaultValue);
    }


    public String getPropertyWithFailback(final String param, final String defaultValue) {
        String result = config.getHierarchycalProperty(param, Sections.COMPARISON, getName(), Sections.SETUP);
        if (result == null) {
            result = taijituConfig.getProperty(param, defaultValue);
        }
        return result;
    }

    public String[] getPropertiesRoot() {
        return new String[]{Sections.COMPARISON, getName()};
    }

    /*** STRATEGY ***/

    public String getStrategyName() {
        return getPropertyWithFailback(Comparison.STRATEGY, DEFAULT_STRATEGY_NAME);
    }

    public StrategyConfig getStrategyConfig() {
        return new StrategyConfigImpl(this);
    }

    /*** PLUGINS ***/

    public String[] getComparisonPluginNames() {
        return StringUtil.splitAndTrim(getPropertyWithFailback(Setup.PLUGINS, DEFAULT_PLUGINS));
    }

    public ComparisonPluginConfig getComparisonPluginConfig(final String pluginName) {
        return new ComparisonPluginConfigImpl(this, pluginName);
    }

    public ComparisonPluginConfig[] getComparisonPluginConfigs() {
        String[] pluginNames = getComparisonPluginNames();
        ComparisonPluginConfig[] result = new ComparisonPluginConfig[pluginNames.length];
        for (int pos = 0; pos < pluginNames.length; pos++) {
            //TODO: May be cached
            result[pos] = new ComparisonPluginConfigImpl(this, pluginNames[pos]);
        }
        return result;
    }

    /**
     * DATABASE
     **/
    public String getDatabaseName() {
        return getPropertyWithFailback(Comparison.DATABASE_REF, null);
    }

    public DatabaseConfig getDatabaseConfig() {
        String databaseName = getDatabaseName();
        if (databaseName != null) {
            return new DatabaseConfigImpl(taijituConfig, databaseName);
        }
        return null;
    }

    /**
     * PROPERTIES
     **/

    public int getFetchSize() {
        try {
            return Integer.valueOf(getPropertyWithFailback(Setup.FETCH_SIZE, Integer.toString(DEFAULT_FETCHSIZE)));
        } catch (final NumberFormatException e) {
            logger.warn("FetchSize can't be parsed. Please, review configuration file.");
        }
        return DEFAULT_FETCHSIZE;
    }

    /**
     * Key fields are fields that define both entries are the same.
     * Some strategies may not need this field (i.e. strategies that assume all
     * entries are returned by both queries, and just need to check the contents)
     */
    public String[] getKeyFields() {
        String property = getPropertyWithFailback(Comparison.Fields.KEY, getName());
        return StringUtil.splitAndTrim(property);
    }


    public String[] getCompareFields() {
        String property = getPropertyWithFailback(Comparison.FIELDS, getName());
        return StringUtil.splitAndTrim(property);
    }

    /**
     * Precission threshold allow numeric columns to have slightly different values, but similar enough to be considered the same.
     * So, if precission threshold is defined, and difference between two numeric columns is lesser than the threshold, no difference will be acknowledged.
     */
    public double getPrecisionThreshold() {
        final String ptString = getPropertyWithFailback(Setup.PRECISION_THRESHOLD, null);
        if (ptString != null) {
            try {
                return Double.parseDouble(ptString);
            } catch (final NumberFormatException e) {
                logger.error("Precision threshold can't be parsed. Using " + DEFAULT_PRECISION_THRESHOLD + " threshold", e);
            }
        }
        return DEFAULT_PRECISION_THRESHOLD;
    }

    /**
     * QUERIES
     **/
    public QueryConfig getSourceQueryConfig() {
        return new QueryConfigImpl(this, Comparison.SOURCE);
    }

    public QueryConfig getTargetQueryConfig() {
        return new QueryConfigImpl(this, Comparison.TARGET);
    }


}
