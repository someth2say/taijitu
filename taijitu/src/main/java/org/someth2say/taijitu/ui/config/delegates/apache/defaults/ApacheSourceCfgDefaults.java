package org.someth2say.taijitu.ui.config.delegates.apache.defaults;

import org.someth2say.taijitu.ui.config.ConfigurationLabels;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;

import java.util.List;
import java.util.Properties;

public interface ApacheSourceCfgDefaults extends ApacheCfgDefaults, ISourceCfg {

    @Override
    default String getType() {
        return getConfiguration().getString(ConfigurationLabels.SOURCE_TYPE);
    }

    @Override
    default List<String> getKeyFields() {
        //TODO: Consider '*' wildcard for key fields
        return getConfiguration().getList(String.class, ConfigurationLabels.KEYS, null);
    }

    @Override
    default Properties getFetchProperties() {
        return getConfiguration().getProperties(ConfigurationLabels.SOURCE_FETCH_PROPERTIES);
    }

    @Override
    default Properties getBuildProperties() {
        return getConfiguration().getProperties(ConfigurationLabels.SOURCE_BUILD_PROPERTIES);
    }

    default String getMapper() {
        return getConfiguration().getString(ConfigurationLabels.MAPPER_TYPE);
    }

}
