package org.someth2say.taijitu.cli.config.delegates.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.delegates.apache.ApacheComparison;
import org.someth2say.taijitu.cli.config.interfaces.ICfg;
import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.cli.util.Named;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_SCAN_CLASSPATH;
import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_THREADS;

public interface ApacheTaijituCfgDefaults extends ApacheCfgDefaults, ITaijituCfg, ApacheComparisonCfgDefaults, ICfg, Named {

    @Override
    default List<IComparisonCfg> getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.COMPARISON);
        if (comparisonConfigs != null) {
            return comparisonConfigs.stream().map(ApacheComparison::new).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    default Integer getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.THREADS, DEFAULT_THREADS);
    }

    @Override
    default Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

}
