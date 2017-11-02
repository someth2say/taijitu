package org.someth2say.taijitu.config.apache.defaults;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.apache.ApacheComparison;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_SCAN_CLASSPATH;

public interface ApacheTaijituConfig extends ApacheConfig, ITaijituCfg, ApacheComparisonConfig, ApachePluginConfig {

    //TODO: Migrate to lists
    @Override
    default IComparisonCfg[] getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.COMPARISON);
        List<ApacheComparison> impls = comparisonConfigs.stream().map(cs -> new ApacheComparison(cs, this)).collect(Collectors.toList());
        return impls.toArray(new IComparisonCfg[0]);
    }

    @Override
    default Integer getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.Setup.THREADS, DEFAULT_THREADS);
    }

    @Override
    default String getConsoleLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    @Override
    default String getFileLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    @Override
    default String getOutputFolder() {
        return getConfiguration().getString(ConfigurationLabels.Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    @Override
    default Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.Setup.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

}
