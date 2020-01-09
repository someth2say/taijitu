package org.someth2say.taijitu.cli.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.someth2say.taijitu.cli.TaijituCliException;
import org.someth2say.taijitu.cli.config.impl.TaijituGfg;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_LIST_DELIMITER;

public class TaijituConfig {
    private TaijituConfig() {
    }


    /**** UTILITIES *********************************************************************/

    public static ITaijituCfg fromYamlFile(final String file) throws TaijituCliException {
        HierarchicalConfiguration config = loadYamlFile(file);
        return new TaijituGfg(config);
    }


    public static ITaijituCfg fromPropertiesFile(final String file) throws TaijituCliException {
        HierarchicalConfiguration config = loadPropertiesFile(file);
        return new TaijituGfg(config);
    }

    public static ITaijituCfg fromApacheConfig(final HierarchicalConfiguration config) {
        return new TaijituGfg(config);
    }

    public static TaijituGfg fromYaml(final String contents) throws TaijituCliException {
        HierarchicalConfiguration config = loadYaml(contents);
        return new TaijituGfg(config);
    }

    public static String toYaml(final TaijituGfg config) throws TaijituCliException {
        StringWriter writer = new StringWriter();
        try {
            (new YAMLConfiguration(config.getConfiguration())).write(writer);
        } catch (ConfigurationException | IOException e) {
            throw new TaijituCliException("Unable to write configuration", e);
        }
        return writer.getBuffer().toString();

    }

    /**
     * Tries to loadPropertiesFile configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to loadPropertiesFile
     */
    private static HierarchicalConfiguration loadPropertiesFile(final String configFile) throws TaijituCliException {
        final PropertiesBuilderParameters builderParameters = new Parameters().properties().setFileName(configFile);
        ConfigurationBuilder<PropertiesConfiguration> builder
                = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(builderParameters);
        try {
            final PropertiesConfiguration pConfig = builder.getConfiguration();
            pConfig.setListDelimiterHandler(new DefaultListDelimiterHandler(DEFAULT_LIST_DELIMITER));
            return ConfigurationUtils.convertToHierarchical(pConfig);
        } catch (ConfigurationException e) {
            throw new TaijituCliException("Unable to build configuration", e);
        }
    }

    private static HierarchicalConfiguration loadYamlFile(final String configFile) throws TaijituCliException {
        final PropertiesBuilderParameters builderParameters = new Parameters().properties().setFileName(configFile);
        ConfigurationBuilder<YAMLConfiguration> builder
                = new FileBasedConfigurationBuilder<>(YAMLConfiguration.class)
                .configure(builderParameters);
        try {
            YAMLConfiguration yamlConfiguration = builder.getConfiguration();
            yamlConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(DEFAULT_LIST_DELIMITER));
            return yamlConfiguration;
        } catch (ConfigurationException e) {
            throw new TaijituCliException("Unable to parse configuration YAML", e);
        }
    }

    private static HierarchicalConfiguration loadYaml(final String yamlContents) throws TaijituCliException {
        try {
            YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
            yamlConfiguration.read(new StringReader(yamlContents));
            return yamlConfiguration;
        } catch (ConfigurationException e) {
            throw new TaijituCliException("Unable to parse configuration YAML", e);
        }
    }

    private static HierarchicalConfiguration loadJson(final String jsonContents) throws TaijituCliException {
        try {
            JSONConfiguration json = new JSONConfiguration();
            json.read(new StringReader(jsonContents));
            return json;
        } catch (ConfigurationException e) {
            throw new TaijituCliException("Unable to parse configuration YAML", e);
        }
    }
}
