package org.someth2say.taijitu.config.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;

public interface ApacheBasedComparisonConfig extends ApacheBasedConfig, ComparisonConfig, DelegatingConfig<ComparisonConfig> {
	
    default public List<EqualityConfig> getEqualityConfigs() {
        final List<ImmutableHierarchicalConfiguration> thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(Comparison.EQUALITY);
        final List<EqualityConfig> equalityConfigs = thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
        final List<EqualityConfig> parentEqualityConfigs = getParent().getEqualityConfigs();
        equalityConfigs.addAll(parentEqualityConfigs);
        return equalityConfigs;
    }
    
	default StrategyConfig getStrategyConfig() {
		try {
		    ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(Comparison.STRATEGY);
		    return new StrategyConfigImpl(strategyConfiguration);
		} catch (IllegalArgumentException | ConfigurationRuntimeException e) {
		    //No Strategy defined (or many)
		    return getParent().getStrategyConfig();
		}
	}
}
