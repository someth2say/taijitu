package org.someth2say.taijitu.cli.config.interfaces;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.impl.EqualityCfg;
import org.someth2say.taijitu.cli.config.impl.SourceGfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.cli.config.DefaultConfig.*;

public interface IComparisonCfg extends ISourceCfg, IEqualityCfg, INamedCfg, ICfg {

    default List<String> getKeyFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.KEYS, DEFAULT_KEY_FIELDS);
    }


    default List<String> getSortFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.SORT, DEFAULT_SORT_FIELDS);
    }


    default List<String> getCompareFields() {
        return getConfiguration().getList(String.class, ConfigurationLabels.COMPARE, DEFAULT_COMPARE_FIELDS);
    }


    default List<IEqualityCfg> getEqualityConfigs() {
        List<? extends HierarchicalConfiguration<?>> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().configurationsAt(ConfigurationLabels.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            return DEFAULT_EQUALITY_CONFIGS;
        }
        return thisEqConfigs.stream().map(EqualityCfg::new).collect(Collectors.toList());
    }


    default List<ISourceCfg> getSourceConfigs() {
        final List<? extends HierarchicalConfiguration<?>> sourceConfigs = this.getConfiguration().configurationsAt(ConfigurationLabels.SOURCES);
        return sourceConfigs.stream().map(SourceGfg::new).collect(Collectors.toList());
    }
    
}
