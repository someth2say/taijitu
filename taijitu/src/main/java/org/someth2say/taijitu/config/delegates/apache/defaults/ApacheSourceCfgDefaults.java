package org.someth2say.taijitu.config.delegates.apache.defaults;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;

import java.util.List;
import java.util.Properties;

public interface ApacheSourceCfgDefaults extends ApacheCfgDefaults, ISourceCfg {

    @Override
    default String getType() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
    }

    @Override
    default List<String> getKeyFields() {
        //TODO: Consider '*' wildcard for key fields
        return getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
    }

    @Override
    default Properties getFetchProperties() {
        //TODO: Investigate how to structure source properties (i.e. databaseProperties) so they can provided by parents
        return getConfiguration().getProperties(ConfigurationLabels.Comparison.SOURCE_FETCH_PROPERTIES);
    }

    @Override
    default Properties getBuildProperties() {
        //TODO: Investigate how to structure source properties (i.e. databaseProperties) so they can provided by parents
        return getConfiguration().getProperties(ConfigurationLabels.Comparison.SOURCE_BUILD_PROPERTIES);
    }


}
