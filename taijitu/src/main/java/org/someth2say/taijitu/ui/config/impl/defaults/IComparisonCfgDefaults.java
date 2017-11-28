package org.someth2say.taijitu.ui.config.impl.defaults;

import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.impl.EqualityCfg;
import org.someth2say.taijitu.ui.config.impl.PluginCfg;
import org.someth2say.taijitu.ui.config.impl.SourceCfg;
import org.someth2say.taijitu.ui.config.impl.StrategyCfg;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.ui.config.interfaces.IStrategyCfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.ui.util.ListUtil.safeUnion;

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

    // Warning: equalities are additive, not failback-ing
    @Override
    default List<IEqualityCfg> getEqualityConfigs() {
        List<IEqualityCfg> delegates = getDelegate().getEqualityConfigs();
        List<IEqualityCfg> equalityCfgs = null;
        if (delegates != null) {
            equalityCfgs = delegates.stream().map(dele -> new EqualityCfg(dele, this)).collect(Collectors.toList());
        }

        List<IEqualityCfg> parentCfgs;
        if (getParent() != null) {
            parentCfgs = getParent().getEqualityConfigs();
        } else {
            parentCfgs = DefaultConfig.DEFAULT_EQUALITY_CONFIG;
        }

        return safeUnion(equalityCfgs, parentCfgs);
    }

    @Override
    default List<IPluginCfg> getPluginConfigs() {
        List<IPluginCfg> delegates = getDelegate().getPluginConfigs();
        List<IPluginCfg> plugins = null;
        if (delegates != null) {
            plugins = delegates.stream().map(dele -> new PluginCfg(dele, this)).collect(Collectors.toList());
        }
        List<IPluginCfg> parentPlugins;
        if (getParent() != null) {
            parentPlugins = getParent().getPluginConfigs();
        } else {
            parentPlugins = DefaultConfig.DEFAULT_PLUGINS_CONFIG;
        }

        return safeUnion(plugins, parentPlugins);


    }

}
