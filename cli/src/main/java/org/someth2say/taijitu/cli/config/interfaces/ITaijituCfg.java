package org.someth2say.taijitu.cli.config.interfaces;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.impl.ComparisonCfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_SCAN_CLASSPATH;
import static org.someth2say.taijitu.cli.config.DefaultConfig.DEFAULT_THREADS;

public interface ITaijituCfg extends IComparisonCfg {

    default List<IComparisonCfg> getComparisons() {
        final List<? extends HierarchicalConfiguration<?>> comparisonConfigs = getConfiguration().configurationsAt(ConfigurationLabels.COMPARISON);
        if (comparisonConfigs != null) {
            return comparisonConfigs.stream().map(ComparisonCfg::new).collect(Collectors.toList());
        }
        return null;
    }

    default Integer getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.THREADS, DEFAULT_THREADS);
    }

    default Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

}
