package org.someth2say.taijitu.config.impl.defaults;

import org.apache.commons.collections4.ListUtils;
import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.impl.EqualityCfg;
import org.someth2say.taijitu.config.impl.PluginCfg;
import org.someth2say.taijitu.config.impl.SourceCfg;
import org.someth2say.taijitu.config.impl.StrategyCfg;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.util.ListUtil.safeUnion;

public interface IComparisonCfgDefaults<T extends IComparisonCfg> extends IComparisonCfg, INamedCfgDefaults<T>, IEqualityCfgDefaults<T>, ISourceCfgDefaults<T>, IStrategyCfgDefaults<T>, IPluginCfgDefaults<T> {

    @Override
    default IStrategyCfg getStrategyConfig() {
        IStrategyCfg delegate = getDelegate().getStrategyConfig();
        return delegate != null ? new StrategyCfg(delegate, this) : getParent() != null ? getParent().getStrategyConfig() : DefaultConfig.DEFAULT_STRATEGY_CONFIG;
    }

    // Warning: Sources are additive, not failback-ing
    @Override
    default List<ISourceCfg> getSourceConfigs() {
        List<ISourceCfg> delegates = getDelegate().getSourceConfigs();
        List<ISourceCfg> localSources = null;
        if (delegates != null) {
            localSources = delegates.stream().map(dele -> new SourceCfg(dele, this)).collect(Collectors.toList());
        }
        List<ISourceCfg> parentSources = null;
        if (getParent() != null) {
            parentSources = getParent().getSourceConfigs();
        }
        return safeUnion(localSources, parentSources);
    }

    @Override
    default String getMatchingStrategyName() {
        String matchingStrategyName = getDelegate().getMatchingStrategyName();
        return matchingStrategyName != null ? matchingStrategyName : getParent() != null ? getParent().getMatchingStrategyName() : DefaultConfig.DEFAULT_MATCHING_STRATEGY_NAME;
    }

    // Warning: equalities are additive, not failback-ing
    @Override
    default List<IEqualityCfg> getEqualityConfigs() {
        List<IEqualityCfg> delegates = getDelegate().getEqualityConfigs();
        List<IEqualityCfg> equalityCfgs = null;
        if (delegates != null) {
            equalityCfgs = delegates.stream().map(dele -> new EqualityCfg(dele, this)).collect(Collectors.toList());
        }

        List<IEqualityCfg> parentCfgs = null;
        if (getParent() != null) {
            parentCfgs = getParent().getEqualityConfigs();
        }

        return safeUnion(equalityCfgs, parentCfgs, DefaultConfig.DEFAULT_EQUALITY_CONFIG);

    }

    @Override
    default List<IPluginCfg> getComparisonPluginConfigs() {
        List<IPluginCfg> delegates = getDelegate().getComparisonPluginConfigs();
        List<IPluginCfg> plugins = null;
        if (delegates != null) {
            plugins = delegates.stream().map(dele -> new PluginCfg(dele, this)).collect(Collectors.toList());
        }
        List<IPluginCfg> parentPlugins = null;
        if (getParent() != null) {
            parentPlugins = getParent().getComparisonPluginConfigs();
        }

        return safeUnion(plugins, parentPlugins, DefaultConfig.DEFAULT_PLUGINS_CONFIG);


    }

}
