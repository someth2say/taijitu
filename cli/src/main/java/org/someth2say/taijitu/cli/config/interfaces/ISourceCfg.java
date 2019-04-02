package org.someth2say.taijitu.cli.config.interfaces;

import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.DefaultConfig;

import java.util.List;
import java.util.Properties;

public interface ISourceCfg extends ICfg, INamedCfg {

    default String getType() {
        return getConfiguration().getString(ConfigurationLabels.SOURCE_TYPE);
    }

    default Properties getFetchProperties() {

        List<? extends HierarchicalConfiguration<?>> configurations = getConfiguration().configurationsAt(ConfigurationLabels.SOURCE_FETCH_PROPERTIES);
        if (configurations.isEmpty())
            return DefaultConfig.DEFAULT_EMPTY_PROPERTIES;
        else
            return ConfigurationConverter.getProperties(configurations.get(0));

    }

    default Properties getBuildProperties() {
        List<? extends HierarchicalConfiguration<?>> configurations = getConfiguration().configurationsAt(ConfigurationLabels.SOURCE_BUILD_PROPERTIES);
        if (configurations.isEmpty())
            return DefaultConfig.DEFAULT_EMPTY_PROPERTIES;
        else
            return ConfigurationConverter.getProperties(configurations.get(0));

    }

    default String getMapper() {
        return getConfiguration().getString(ConfigurationLabels.MAPPER_TYPE);
    }

}
