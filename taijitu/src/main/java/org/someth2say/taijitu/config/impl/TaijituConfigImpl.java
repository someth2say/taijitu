package org.someth2say.taijitu.config.impl;

import static org.someth2say.taijitu.config.DefaultConfig.DATE_TIME_FORMATTER;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_COLUMN_MATCHING_STRATEGY_NAME;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_CONSOLE_LOG_LEVEL;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_EQUALITY_CONFIG;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FETCHSIZE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_FILE_LOG_LEVEL;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_OUTPUT_FOLDER;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_QUERY_PARAMETERS;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_SCAN_CLASSPATH;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_STRATEGY_CONFIG;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_THREADS;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Sections;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.config.TaijituConfig;

public final class TaijituConfigImpl implements TaijituConfig, ComparisonConfig {

    private static final Logger logger = Logger.getLogger(TaijituConfigImpl.class);

    private final ImmutableHierarchicalConfiguration configuration;

    private TaijituConfigImpl(final ImmutableHierarchicalConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    public static TaijituConfig fromFile(final String file) throws TaijituException {
        ImmutableHierarchicalConfiguration config = load(file);
        return new TaijituConfigImpl(config);
    }


    public static TaijituConfig fromProperties(final ImmutableHierarchicalConfiguration config) throws TaijituException {
        return new TaijituConfigImpl(config);
    }

    /**** COMPARISONS *********************************************************************/
    @Override
    public ComparisonConfig[] getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(Sections.COMPARISON);
        ComparisonConfig[] result = new ComparisonConfig[comparisonConfigs.size()];
        int pos = 0;
        for (ImmutableHierarchicalConfiguration comparisonConfig : comparisonConfigs) {
            result[pos++] = new ComparisonConfigImpl(comparisonConfig, this);
        }
        return result;
    }

    /**** DATABASE *********************************************************************/

    @Override
    public DatabaseConfig[] getAllDatabaseConfigs() {
        final List<ImmutableHierarchicalConfiguration> databaseConfigs = getConfiguration().immutableChildConfigurationsAt(Sections.DATABASE);
        DatabaseConfig[] result = new DatabaseConfig[databaseConfigs.size()];
        int pos = 0;
        for (ImmutableHierarchicalConfiguration databaseConfig : databaseConfigs) {
            result[pos++] = new DatabaseConfigImpl(databaseConfig);
        }
        return result;
    }

    @Override
    public String getDatabaseRef() {
        String dbRef = getConfiguration().getString(Comparison.DATABASE_REF, null);
        if (dbRef == null) {
            //If no dbRef defined, just use a reference to the first database defined
            final List<ImmutableHierarchicalConfiguration> dbConfigs = getConfiguration().immutableChildConfigurationsAt(Sections.DATABASE);
            if (!dbConfigs.isEmpty()) {
                dbRef = dbConfigs.get(0).getRootElementName();
            }
        }
        return dbRef;
    }

    @Override
    public String[] getKeyFields() {
        return getConfiguration().get(String[].class, Comparison.Fields.KEY, null);
    }

    @Override
    public String getColumnMatchingStrategyName() {
        return getConfiguration().getString(Setup.COLUMN_MATCHING_STRATEGY, DEFAULT_COLUMN_MATCHING_STRATEGY_NAME);
    }

    @Override
    public String getStatement() {
        return getConfiguration().getString(Comparison.STATEMENT, null);
    }

    /**
     * QUERIES
     ****************************************************************************/
    @Override
    public QueryConfig getSourceQueryConfig() {
        try {
            final ImmutableHierarchicalConfiguration sourceConfig = this.getConfiguration().immutableConfigurationAt(Comparison.SOURCE);
            return new QueryConfigImpl(sourceConfig, this);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public QueryConfig getTargetQueryConfig() {
        try {
            final ImmutableHierarchicalConfiguration sourceConfig = this.getConfiguration().immutableConfigurationAt(Comparison.TARGET);
            return new QueryConfigImpl(sourceConfig, this);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**** SETUP *********************************************************************/

    /**
     * Number of threads Taijitu will raise for running comparisons.
     * Note that each comparison strategy may require different number of threads.
     */
    @Override
    public int getThreads() {
        return getConfiguration().getInt(Setup.THREADS, DEFAULT_THREADS);
    }

    @Override
    public String getConsoleLog() {
        return getConfiguration().getString(Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    @Override
    public String getFileLog() {
        return getConfiguration().getString(Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    @Override
    public String getOutputFolder() {
        return getConfiguration().getString(Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    @Override
    public int getFetchSize() {
        return getConfiguration().getInt(Setup.FETCH_SIZE, DEFAULT_FETCHSIZE);
    }

    @Override
    public Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(Setup.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

    @Override
    public PluginConfig[] getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(Sections.PLUGINS);
        PluginConfig[] result = new PluginConfig[pluginConfigs.size()];
        int pos = 0;
        for (ImmutableHierarchicalConfiguration pluginConfig : pluginConfigs) {
            result[pos++] = new PluginConfigImpl(pluginConfig);
        }
        return result;
    }

    public Object[] getQueryParameters() {
        return getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, DEFAULT_QUERY_PARAMETERS);
    }

    @Override
    public List<EqualityConfig> getEqualityConfigs() {
        final List<ImmutableHierarchicalConfiguration> thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(Comparison.EQUALITY);
        final List<EqualityConfig> equalityConfigs = thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
        // Default equality strategy should be always present.
        equalityConfigs.add(DEFAULT_EQUALITY_CONFIG);
        return equalityConfigs;
    }

    /**** STRATEGY *********************************************************************/

    // TODO: Reconsider memoization strategy
    private StrategyConfig strategyConfig = null;

    @Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            try {
                ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(Comparison.STRATEGY);
                strategyConfig = new StrategyConfigImpl(strategyConfiguration);
            } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
                //No Strategy defined (or many)
                return DEFAULT_STRATEGY_CONFIG;
            }
        }
        return strategyConfig;
    }


    /**** UTILITIES *********************************************************************/

    public File getOutputFolderFile() {
        return new File(getOutputFolder());
    }

    public static Date parseDate(String dateStr) {
        if (dateStr != null) {
            try {
                return new Date(DATE_TIME_FORMATTER.parseMillis(dateStr));
            } catch (final IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Tries to load configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to load
     */
    private static ImmutableHierarchicalConfiguration load(final String configFile) throws TaijituException {
        final PropertiesBuilderParameters builderParameters = new Parameters().properties().setFileName(configFile);
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(builderParameters);
        return buildConfiguration(configFile, builder);
    }

    private static ImmutableHierarchicalConfiguration buildConfiguration(String configFile, FileBasedConfigurationBuilder<PropertiesConfiguration> builder) throws TaijituException {
        try {
            final PropertiesConfiguration pConfig = builder.getConfiguration();
            final HierarchicalConfiguration<?> hConfig = ConfigurationUtils.convertToHierarchical(pConfig);
            return ConfigurationUtils.unmodifiableConfiguration(hConfig);
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            throw new TaijituException("Unable to load properties from " + configFile, e);
        }
    }

    @Override
    public String getName() {
        return null;
    }

	public ImmutableHierarchicalConfiguration getConfiguration() {
		return configuration;
	}
}
