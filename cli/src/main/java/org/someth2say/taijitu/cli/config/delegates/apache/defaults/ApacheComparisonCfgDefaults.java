package org.someth2say.taijitu.cli.config.delegates.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.delegates.apache.ApacheEquality;
import org.someth2say.taijitu.cli.config.delegates.apache.ApachePlugin;
import org.someth2say.taijitu.cli.config.delegates.apache.ApacheSource;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ApacheComparisonCfgDefaults extends ApacheNamedCfgDefaults, IComparisonCfg, ApacheEqualityCfgDefaults, ApacheSourceCfgDefaults, ApacheStrategyCfgDefaults, ApachePluginCfgDefaults {


    @Override
    default List<String> getKeyFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.KEYS, null);
    }

    @Override
    default List<String> getSortFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.SORT, null);
    }

    @Override
    default List<String> getCompareFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.COMPARE, null);
    }

    @Override
    default List<IEqualityCfg> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(ApacheEquality::new).collect(Collectors.toList());
    }

    @Override
    default List<ISourceCfg> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.SOURCES);
        return sourceConfigs.stream().map(ApacheSource::new).collect(Collectors.toList());
    }

    @Override
    default List<IPluginCfg> getPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.PLUGINS);
        return pluginConfigs.stream().map(ApachePlugin::new).collect(Collectors.toList());
    }

}
