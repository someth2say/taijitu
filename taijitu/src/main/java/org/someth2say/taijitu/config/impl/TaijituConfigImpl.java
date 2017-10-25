package org.someth2say.taijitu.config.impl;

import static org.someth2say.taijitu.config.DefaultConfig.DATE_TIME_FORMATTER;
import java.io.File;
import java.util.Date;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedTaijituConfig;

public final class TaijituConfigImpl implements ApacheBasedTaijituConfig {

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

    /**** STRATEGY *********************************************************************/

    // TODO: Reconsider memoization strategy
    private StrategyConfig strategyConfig = null;

    @Override
    public ComparisonConfig getParent() {
        return null;
    }

    @Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            strategyConfig = ApacheBasedTaijituConfig.super.getStrategyConfig();
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
	public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String getName() {
        return null;
    }
}