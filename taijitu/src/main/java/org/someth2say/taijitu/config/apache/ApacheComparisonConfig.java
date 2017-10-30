package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.delegate.ComparisonConfigDelegate;
import org.someth2say.taijitu.config.delegate.SourceConfigDelegate;
import org.someth2say.taijitu.config.delegating.EqualityConfigIface;
import org.someth2say.taijitu.config.impl.EqualityConfigImpl;
import org.someth2say.taijitu.config.impl.SourceConfigImpl;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApacheComparisonConfig extends ApacheSourceConfig implements ComparisonConfigDelegate {

    ApacheComparisonConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public StrategyConfigIface getStrategyConfig() {
        ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Comparison.STRATEGY);
        return new ApacheStrategyConfig(strategyConfiguration);
    }


    @Override
    public String getMatchingStrategyName() {
        return getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
    }

    @Override
    public List<EqualityConfigIface> getEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(ApacheEqualityConfig::new).map(EqualityConfigImpl::new).collect(Collectors.toList());
    }

    @Override
    public List<SourceConfigIface<SourceConfigIface, SourceConfigDelegate>> getSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.SOURCES);
        return sourceConfigs.stream().map(ApacheSourceConfig::new).map((Function<ApacheSourceConfig, SourceConfigImpl>) SourceConfigImpl::new).collect(Collectors.toList());
    }

//    private SourceConfigIface<SourceConfigIface, SourceConfigDelegate> buildSpecificConfig(final ImmutableHierarchicalConfiguration config) {
//        //TODO: May we have some kind of configuration registry (but just for this apache implementation)?
//        String sourceType = config.getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
//        switch (sourceType) {
//            case ResultSetSource.NAME:
//                return new ApacheQuerySourceConfig(config, this);
//            case CSVFileSource.NAME:
//                ApacheFileSourceConfig delegate = new ApacheFileSourceConfig(config, this);
//                return new FileSourceConfigImpl(this, delegate);
//            default:
//                return null;
//        }
//    }
}

