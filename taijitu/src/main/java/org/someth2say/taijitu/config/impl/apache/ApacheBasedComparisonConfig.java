package org.someth2say.taijitu.config.impl.apache;

import java.beans.Expression;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.impl.EqualityConfigImpl;
import org.someth2say.taijitu.config.impl.FileSourceConfigImpl;
import org.someth2say.taijitu.config.impl.QuerySourceConfigImpl;
import org.someth2say.taijitu.config.impl.StrategyConfigImpl;
import org.someth2say.taijitu.registry.SourceTypeRegistry;
import org.someth2say.taijitu.source.CSVFileSource;
import org.someth2say.taijitu.source.ResultSetSource;
import org.someth2say.taijitu.source.Source;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public interface ApacheBasedComparisonConfig extends ComparisonConfig, ApacheBasedQuerySourceConfig, ApacheBasedFileSourceConfig {

    ComparisonConfig getParent();

    @Override
    default List<EqualityConfig> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        final List<EqualityConfig> equalityConfigs = thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
        if (getParent() != null) {
            equalityConfigs.addAll(getParent().getEqualityConfigs());
        } else {
            equalityConfigs.add(DEFAULT_EQUALITY_CONFIG);
        }
        return equalityConfigs;
    }

    @Override
    default StrategyConfig getStrategyConfig() {
        try {
            ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(Comparison.STRATEGY);
            return new StrategyConfigImpl(strategyConfiguration);
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            //No Strategy defined (or many)
            return getParent() != null ? getParent().getStrategyConfig() : DEFAULT_STRATEGY_CONFIG;
        }
    }

    @Override
    default PluginConfig[] getComparisonPluginConfigs() {
        // right now, we only allow global plugins. Maybe we can allow per-comparison plugins someday...
        return getParent() != null ? getParent().getComparisonPluginConfigs() : DEFAULT_PLUGINS_CONFIG;
    }

    @Override
    default String getMatchingStrategyName() {
        String matchingStrategy = getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
        return matchingStrategy != null ? matchingStrategy
                : getParent() != null ? getParent().getMatchingStrategyName() : DEFAULT_MATCHING_STRATEGY_NAME;
    }


//    @Override
//    default SourceConfig getSourceConfig(final String sourceId) {
//        try {
//            final ImmutableHierarchicalConfiguration sourceConfig = this.getConfiguration().immutableConfigurationAt(sourceId);
//            //TODO: Discriminate the kind of source.
//            return new QuerySourceConfigImpl(sourceConfig, this);
//        } catch (IllegalArgumentException e) {
//            return getParent() != null ? getParent().getSourceConfig(sourceId) : null;
//        }
//    }
//
    @Override
    default List<SourceConfig> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(Comparison.SOURCES);
        List<SourceConfig> localSourceConfigs = sourceConfigs.stream().map(this::buildSpecificConfig).collect(Collectors.toList());
        if (getParent() != null) localSourceConfigs.addAll(getParent().getSourceConfigs());
        return localSourceConfigs;
        //return new QuerySourceConfigImpl(sourceConfigs, this);

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
