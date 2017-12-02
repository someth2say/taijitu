package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.config.DefaultConfig;
import org.someth2say.taijitu.cli.config.impl.EqualityCfg;
import org.someth2say.taijitu.cli.config.impl.PluginCfg;
import org.someth2say.taijitu.cli.config.impl.SourceCfg;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.cli.util.ListUtil.safeUnion;

public interface IComparisonCfgDefaults<T extends IComparisonCfg> extends IComparisonCfg, INamedCfgDefaults<T>, IEqualityCfgDefaults<T>, ISourceCfgDefaults<T>, IStrategyCfgDefaults<T>, IPluginCfgDefaults<T> {

    @Override
    default List<String> getKeyFields() {
        List<String> keyFields = getDelegate().getKeyFields();
        return keyFields != null ? keyFields
                : getParent() != null ? getParent().getKeyFields() : DefaultConfig.DEFAULT_KEY_FIELDS;
    }

    @Override
    default List<String> getSortFields() {
        List<String> sortFields = getDelegate().getSortFields();
        return sortFields != null ? sortFields
                : getParent() != null ? getParent().getSortFields() : DefaultConfig.DEFAULT_SORT_FIELDS;
    }

    @Override
    default List<String> getCompareFields() {
        List<String> compareFields = getDelegate().getCompareFields();
        return compareFields != null ? compareFields
                : getParent() != null ? getParent().getCompareFields() : DefaultConfig.DEFAULT_COMPARE_FIELDS;
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
