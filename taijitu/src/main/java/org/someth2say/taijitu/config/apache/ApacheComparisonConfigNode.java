package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.EqualityConfigImpl;
import org.someth2say.taijitu.source.CSVFileSource;
import org.someth2say.taijitu.source.ResultSetSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ApacheComparisonConfigNode extends ApacheSourceConfigNode implements ComparisonConfig {

    ApacheComparisonConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheTaijituConfigNode parent) {
        super(configuration, parent);
    }

    @Override
    public StrategyConfig getStrategyConfig() {
        ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Comparison.STRATEGY);
        return new ApacheStrategyConfigNode(strategyConfiguration);
    }

    @Override
    public PluginConfig[] getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        List<ApachePluginConfigNode> impls = pluginConfigs.stream().map(ApachePluginConfigNode::new).collect(Collectors.toList());
        return impls.toArray(new PluginConfig[0]);
    }

    @Override
    public String getMatchingStrategyName() {
        return getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
    }

    @Override
    public List<EqualityConfig> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
    }

    @Override
    public List<SourceConfig> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.SOURCES);
        return sourceConfigs.stream().map(this::buildSpecificConfig).collect(Collectors.toList());
    }

    private SourceConfig buildSpecificConfig(final ImmutableHierarchicalConfiguration config) {
        //TODO: May we have some kind of configuration registry (but just for this apache implementation)?
        String sourceType = config.getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
        switch (sourceType) {
            case ResultSetSource.NAME:
                return new ApacheQuerySourceConfigNode(config, this);
            case CSVFileSource.NAME:
                return new ApacheFileSourceConfigNode(config, this);
            default:
                return null;
        }
    }
}

