package org.someth2say.taijitu.cli.config.impl.defaults;

import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.Properties;

public interface IApacheSourceCfg extends IApacheCfg, ISourceCfg {

    @Override
    default String getType() {
        return getConfiguration().getString(ConfigurationLabels.SOURCE_TYPE);
    }

    @Override
    default Properties getFetchProperties() {
        HierarchicalConfiguration<?> buildPropsConfig = getConfiguration().configurationAt(ConfigurationLabels.SOURCE_FETCH_PROPERTIES);
        return ConfigurationConverter.getProperties(buildPropsConfig);

    }

    @Override
    default Properties getBuildProperties() {
        HierarchicalConfiguration<?> buildPropsConfig = getConfiguration().configurationAt(ConfigurationLabels.SOURCE_BUILD_PROPERTIES);
        return ConfigurationConverter.getProperties(buildPropsConfig);
    }

    @Override
	default String getMapper() {
        return getConfiguration().getString(ConfigurationLabels.MAPPER_TYPE);
    }

}
