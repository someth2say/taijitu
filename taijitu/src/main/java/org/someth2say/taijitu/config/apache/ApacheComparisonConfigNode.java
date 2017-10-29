package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.PluginConfigImpl;

import java.util.List;
import java.util.stream.Collectors;

public class ApacheComparisonConfigNode extends ApacheQuerySourceConfigNode, ApacheFileSourceConfigNode implements ComparisonConfig {
    public ApacheComparisonConfigNode(ImmutableHierarchicalConfiguration cs, ApacheTaijituConfigNode apacheTaijituConfigNode) {
    }

    @Override
    public StrategyConfig getStrategyConfig() {
        return null;
    }

    @Override
    public PluginConfig[] getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        List<PluginConfigImpl> impls = pluginConfigs.stream().map(ApachePluginConfigNode::new).collect(Collectors.toList());
        return impls.toArray(new PluginConfig[0]);
    }

    @Override
    public String getMatchingStrategyName() {
        return null;
    }

    @Override
    public List<EqualityConfig> getEqualityConfigs() {
        return null;
    }

    @Override
    public List<SourceConfig> getSourceConfigs() {
        return null;
    }
}

