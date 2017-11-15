package org.someth2say.taijitu.config.delegates.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.delegates.apache.ApacheEquality;
import org.someth2say.taijitu.config.delegates.apache.ApachePlugin;
import org.someth2say.taijitu.config.delegates.apache.ApacheSource;
import org.someth2say.taijitu.config.delegates.apache.ApacheStrategy;
import org.someth2say.taijitu.config.interfaces.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ApacheComparisonCfgDefaults extends ApacheNamedCfgDefaults, IComparisonCfg, ApacheEqualityCfgDefaults, ApacheSourceCfgDefaults, ApacheStrategyCfgDefaults, ApachePluginCfgDefaults {

    @Override
    default IStrategyCfg getStrategyConfig() {
        try {
            ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Comparison.STRATEGY);
            return new ApacheStrategy(strategyConfiguration);
        } catch (ConfigurationRuntimeException e){
            return null;
        }
    }

    @Override
    default List<IEqualityCfg> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(ApacheEquality::new).collect(Collectors.toList());
    }

    @Override
    default List<ISourceCfg> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.SOURCES);
        return sourceConfigs.stream().map(ApacheSource::new).collect(Collectors.toList());
    }

    @Override
    default List<IPluginCfg> getPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        return pluginConfigs.stream().map(ApachePlugin::new).collect(Collectors.toList());
    }

}
