package org.someth2say.taijitu.config.impl.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.impl.EqualityConfigImpl;
import org.someth2say.taijitu.config.impl.FileSourceConfigImpl;
import org.someth2say.taijitu.config.impl.QuerySourceConfigImpl;
import org.someth2say.taijitu.config.impl.StrategyConfigImpl;
import org.someth2say.taijitu.source.CSVFileSource;
import org.someth2say.taijitu.source.ResultSetSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public interface ApacheBasedComparisonConfig extends ComparisonConfig, ApacheBasedQuerySourceConfig, ApacheBasedFileSourceConfig {

    @Override
	ComparisonConfig getParent();

    @Override
    default List<EqualityConfig> getEqualityConfigs() {
        final List<EqualityConfig> equalityConfigs = getDelegatedEqualityConfigs();
        final List<EqualityConfig> parentEqualityConfigs = getParentEqualityConfigs();
        equalityConfigs.addAll(parentEqualityConfigs);
        return equalityConfigs;
    }

    private List<EqualityConfig> getDelegatedEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
    }

    private List<EqualityConfig> getParentEqualityConfigs() {
        List<EqualityConfig> parentEqualityConfigs;
        if (getParent() != null) {
            parentEqualityConfigs = getParent().getEqualityConfigs();
        } else {
            parentEqualityConfigs= List.of(DEFAULT_EQUALITY_CONFIG);
        }
        return parentEqualityConfigs;
    }

    @Override
    default StrategyConfig getStrategyConfig() {
        try {
            return getDelegatedStrategyConfig();
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            //No Strategy defined (or many)
            return getParent() != null ? getParent().getStrategyConfig() : DEFAULT_STRATEGY_CONFIG;
        }
    }

    private StrategyConfig getDelegatedStrategyConfig() {
        ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(Comparison.STRATEGY);
        return new StrategyConfigImpl(strategyConfiguration);
    }

    @Override
    default PluginConfig[] getComparisonPluginConfigs() {
        // right now, we only allow global plugins. Maybe we can allow per-comparison plugins someday...
        return getParent() != null ? getParent().getComparisonPluginConfigs() : DEFAULT_PLUGINS_CONFIG;
    }

    @Override
    default String getMatchingStrategyName() {
        String matchingStrategy = getDelegatedString();
        return matchingStrategy != null ? matchingStrategy
                : getParent() != null ? getParent().getMatchingStrategyName() : DEFAULT_MATCHING_STRATEGY_NAME;
    }

    private String getDelegatedString() {
        return getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
    }

    @Override
    default List<SourceConfig> getSourceConfigs() {
        List<SourceConfig> localSourceConfigs = getDelegatedSourceConfigs();
        if (getParent() != null) localSourceConfigs.addAll(getParent().getSourceConfigs());
        return localSourceConfigs;
    }

    private List<SourceConfig> getDelegatedSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(Comparison.SOURCES);
        return sourceConfigs.stream().map(this::buildSpecificConfig).collect(Collectors.toList());
    }

    private SourceConfig buildSpecificConfig(final ImmutableHierarchicalConfiguration config) {
        //TODO: May we have some kind of configuration registry (but just for this apache implementation)?
        String sourceType = config.getString(Comparison.SOURCE_TYPE);
        switch (sourceType) {
            case ResultSetSource.NAME:
                return new QuerySourceConfigImpl(config, this);
            case CSVFileSource.NAME:
                return new FileSourceConfigImpl(config, this);
            default:
                return null;
        }
    }
}
