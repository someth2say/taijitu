package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.delegate.TaijituConfigDelegate;
import org.someth2say.taijitu.config.ConfigurationLabels;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public class ApacheTaijituConfig extends ApacheComparisonConfig implements TaijituConfigDelegate {

    private ApacheTaijituConfig(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    //TODO: Migrate to lists
    @Override
    public ComparisonConfigIface[] getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.COMPARISON);
        List<ApacheComparisonConfig> impls = comparisonConfigs.stream().map(cs -> new ApacheComparisonConfig(cs)).collect(Collectors.toList());
        return impls.toArray(new ComparisonConfigIface[0]);
    }

    @Override
    public Integer getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.Setup.THREADS, DEFAULT_THREADS);
    }

    @Override
    public String getConsoleLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    @Override
    public String getFileLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    @Override
    public String getOutputFolder() {
        return getConfiguration().getString(ConfigurationLabels.Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    @Override
    public Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.Setup.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

    @Override
    public PluginConfigIface[] getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        List<ApachePluginConfig> impls = pluginConfigs.stream().map(ApachePluginConfig::new).collect(Collectors.toList());
        return impls.toArray(new PluginConfigIface[0]);
    }

    /**** UTILITIES *********************************************************************/

    public static ApacheTaijituConfig fromFile(final String file) throws TaijituException {
        ImmutableHierarchicalConfiguration config = load(file);
        return new ApacheTaijituConfig(config);
    }

    public static ApacheTaijituConfig fromApacheConfig(final ImmutableHierarchicalConfiguration config) throws TaijituException {
        return new ApacheTaijituConfig(config);
    }

    /**
     * Tries to load configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to load
     */
    private static ImmutableHierarchicalConfiguration load(final String configFile) throws TaijituException {
        final PropertiesBuilderParameters builderParameters = new Parameters().properties().setFileName(configFile);
        //TODO: Discriminate different types of files (properties, YAML, XML...)
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
}
