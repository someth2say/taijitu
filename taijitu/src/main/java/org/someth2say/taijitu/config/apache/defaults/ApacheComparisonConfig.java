package org.someth2say.taijitu.config.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.apache.ApacheEquality;
import org.someth2say.taijitu.config.apache.ApachePlugin;
import org.someth2say.taijitu.config.apache.ApacheSource;
import org.someth2say.taijitu.config.apache.ApacheStrategy;
import org.someth2say.taijitu.config.interfaces.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ApacheComparisonConfig extends ApacheNamedConfig, IComparisonCfg, ApacheEqualityConfig, ApacheSourceConfig, ApacheStrategyConfig, ApachePluginConfig {

    @Override
    default IStrategyCfg getStrategyConfig() {
        try {
            ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Comparison.STRATEGY);
            return new ApacheStrategy(strategyConfiguration, this);
        } catch (ConfigurationRuntimeException e){
            return null;
        }
    }

    @Override
    default String getMatchingStrategyName() {
        return getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
    }

    @Override
    default List<IEqualityCfg> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(ec -> new ApacheEquality(ec, this)).collect(Collectors.toList());
    }

    @Override
    default List<ISourceCfg> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.SOURCES);
        return sourceConfigs.stream().map(configuration -> new ApacheSource(configuration, this)).collect(Collectors.toList());
    }

    @Override
    default List<IPluginCfg> getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        return pluginConfigs.stream().map(pc -> new ApachePlugin(pc, this)).collect(Collectors.toList());
    }

}
