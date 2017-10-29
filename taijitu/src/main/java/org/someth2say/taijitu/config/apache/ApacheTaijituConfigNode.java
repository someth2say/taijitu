package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.TaijituConfig;

import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public class ApacheTaijituConfigNode extends ApacheComparisonConfigNode implements TaijituConfig {

    public ApacheTaijituConfigNode(final ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    //TOD: Migrate to lists
    @Override
    public ComparisonConfig[] getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.COMPARISON);
        List<ApacheComparisonConfigNode> impls = comparisonConfigs.stream().map(cs -> new ApacheComparisonConfigNode(cs, this)).collect(Collectors.toList());
        return impls.toArray(new ComparisonConfig[0]);
    }

    @Override
    public int getThreads() {
        return getConfiguration().getInt(ConfigurationLabels.Setup.THREADS, DEFAULT_THREADS);
    }

    @Override
    public String getConsoleLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.CONSOLE_LOG, DEFAULT_CONSOLE_LOG_LEVEL);
    }

    @Override
    public String getFileLog() {
        return getConfiguration().getString(ConfigurationLabels.Setup.FILE_LOG, DEFAULT_FILE_LOG_LEVEL);
    }

    @Override
    public String getOutputFolder() {
        return getConfiguration().getString(ConfigurationLabels.Setup.OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
    }

    @Override
    public Boolean isUseScanClassPath() {
        return getConfiguration().getBoolean(ConfigurationLabels.Setup.SCAN_CLASSPATH, DEFAULT_SCAN_CLASSPATH);
    }

}
