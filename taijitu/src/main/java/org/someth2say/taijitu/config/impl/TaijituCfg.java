package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.delegates.apache.ApacheTaijitu;
import org.someth2say.taijitu.config.impl.defaults.ITaijituCfgDefaults;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_LIST_DELIMITER;

public class TaijituCfg
        extends Cfg<ITaijituCfg>
        implements ITaijituCfgDefaults<ITaijituCfg> {

    public TaijituCfg(ITaijituCfg delegate) {
        super(delegate, null);
    }

    /**** UTILITIES *********************************************************************/

    public static TaijituCfg fromFile(final String file) throws TaijituException {
        ImmutableHierarchicalConfiguration config = load(file);
        return new TaijituCfg(new ApacheTaijitu(config));
    }

    public static TaijituCfg fromApacheConfig(final ImmutableHierarchicalConfiguration config) throws TaijituException {
        return new TaijituCfg(new ApacheTaijitu(config));
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
            pConfig.setListDelimiterHandler(new DefaultListDelimiterHandler(DEFAULT_LIST_DELIMITER));
            final HierarchicalConfiguration<?> hConfig = ConfigurationUtils.convertToHierarchical(pConfig);
            return ConfigurationUtils.unmodifiableConfiguration(hConfig);
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            throw new TaijituException("Unable to load properties from " + configFile, e);
        }
    }
}
