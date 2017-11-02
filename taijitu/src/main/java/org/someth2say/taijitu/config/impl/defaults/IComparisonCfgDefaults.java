package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.impl.EqualityCfg;
import org.someth2say.taijitu.config.impl.PluginCfg;
import org.someth2say.taijitu.config.impl.SourceCfg;
import org.someth2say.taijitu.config.impl.StrategyCfg;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;

import java.util.List;
import java.util.stream.Collectors;

public interface IComparisonCfgDefaults<T extends IComparisonCfg> extends IComparisonCfg, INamedCfgDefaults<T>, IEqualityCfgDefaults<T>, ISourceCfgDefaults<T>, IStrategyCfgDefaults<T>, IPluginCfgDefaults<T> {

    @Override
    default org.someth2say.taijitu.config.interfaces.IStrategyCfg getStrategyConfig() {
        org.someth2say.taijitu.config.interfaces.IStrategyCfg delegate = getDelegate().getStrategyConfig();
        return delegate != null ? new StrategyCfg(delegate, this) : getParent() != null ? getParent().getStrategyConfig() : DefaultConfig.DEFAULT_STRATEGY_CONFIG;
    }

    // Warning: Sources are additive, not failback-ing
    @Override
    default List<org.someth2say.taijitu.config.interfaces.ISourceCfg> getSourceConfigs() {
        List<org.someth2say.taijitu.config.interfaces.ISourceCfg> delegates = getDelegate().getSourceConfigs();
        List<org.someth2say.taijitu.config.interfaces.ISourceCfg> localSources = delegates.stream().map(dele -> new SourceCfg(dele, this)).collect(Collectors.toList());
        if (getParent() != null) {
            localSources.addAll(getParent().getSourceConfigs());
        } else {
            //TODO: May be there is any default sources?
        }
        return localSources;
    }

    @Override
    default String getMatchingStrategyName() {
        String matchingStrategyName = getDelegate().getMatchingStrategyName();
        return matchingStrategyName != null ? matchingStrategyName : getParent() != null ? getParent().getMatchingStrategyName() : DefaultConfig.DEFAULT_MATCHING_STRATEGY_NAME;
    }

    // Warning: equalities are additive, not failback-ing
    @Override
    default List<org.someth2say.taijitu.config.interfaces.IEqualityCfg> getEqualityConfigs() {
        List<org.someth2say.taijitu.config.interfaces.IEqualityCfg> equalityConfigs = getDelegate().getEqualityConfigs();

        List<org.someth2say.taijitu.config.interfaces.IEqualityCfg> equalities = equalityConfigs.stream().map(dele -> new EqualityCfg(dele, this)).collect(Collectors.toList());
        if (getParent() != null) {
            equalities.addAll(getParent().getEqualityConfigs());
        } else {
            equalities.add(DefaultConfig.DEFAULT_EQUALITY_CONFIG);
        }

        return equalities;
    }

    @Override
    default List<org.someth2say.taijitu.config.interfaces.IPluginCfg> getComparisonPluginConfigs() {
        List<org.someth2say.taijitu.config.interfaces.IPluginCfg> delegates = getDelegate().getComparisonPluginConfigs();
        List<org.someth2say.taijitu.config.interfaces.IPluginCfg> plugins = delegates.stream().map(dele -> new PluginCfg(dele, this)).collect(Collectors.toList());
        if (getParent()!=null){
            plugins.addAll(getParent().getComparisonPluginConfigs());
        } else {
            plugins.addAll(DefaultConfig.DEFAULT_PLUGINS_CONFIG);
        }
        return plugins;
    }
}
