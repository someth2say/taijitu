package org.someth2say.taijitu.config.delegates.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.delegates.apache.ApacheComparison;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public interface ApacheTaijituCfgDefaults extends ApacheCfgDefaults, ITaijituCfg, ApacheComparisonCfgDefaults, ApachePluginCfgDefaults {

    @Override
    default List<IComparisonCfg> getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.COMPARISON);
        if (comparisonConfigs != null) {
            return comparisonConfigs.stream().map(ApacheComparison::new).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    default Integer getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.Setup.THREADS, DEFAULT_THREADS);
    }

    @Override
    default Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.Setup.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

}
