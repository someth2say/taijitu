package org.someth2say.taijitu.cli.config.delegates.apache.defaults;

import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.Properties;

public interface ApacheSourceCfgDefaults extends ApacheCfgDefaults, ISourceCfg {

    @Override
    default String getType() {
        return getConfiguration().getString(ConfigurationLabels.SOURCE_TYPE);
    }

    @Override
    default Properties getFetchProperties() {
        return getConfiguration().getProperties(ConfigurationLabels.SOURCE_FETCH_PROPERTIES);
    }

    @Override
    default Properties getBuildProperties() {
        return getConfiguration().getProperties(ConfigurationLabels.SOURCE_BUILD_PROPERTIES);
    }

    @Override
	default String getMapper() {
        return getConfiguration().getString(ConfigurationLabels.MAPPER_TYPE);
    }

}