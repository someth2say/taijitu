package org.someth2say.taijitu.config.impl.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.ComparisonConfigImpl;
import org.someth2say.taijitu.config.impl.DatabaseConfigImpl;
import org.someth2say.taijitu.config.impl.PluginConfigImpl;

import java.util.List;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public interface ApacheBasedTaijituConfig extends TaijituConfig, ApacheBasedComparisonConfig {

    @Override
    default ComparisonConfig[] getComparisons() {
        final List<ImmutableHierarchicalConfiguration> comparisonConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.COMPARISON);
        ComparisonConfig[] result = new ComparisonConfig[comparisonConfigs.size()];
        int pos = 0;
        for (ImmutableHierarchicalConfiguration comparisonConfig : comparisonConfigs) {
            result[pos++] = new ComparisonConfigImpl(comparisonConfig, this);
        }
        return result;
    }

//    @Override
//    default DatabaseConfig[] getAllDatabaseConfigs() {
//        final List<ImmutableHierarchicalConfiguration> databaseConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.DATABASE);
//        DatabaseConfig[] result = new DatabaseConfig[databaseConfigs.size()];
//        int pos = 0;
//        for (ImmutableHierarchicalConfiguration databaseConfig : databaseConfigs) {
//            result[pos++] = new DatabaseConfigImpl(databaseConfig);
//        }
//        return result;
//    }

    @Override
    default PluginConfig[] getComparisonPluginConfigs() {
        final List<ImmutableHierarchicalConfiguration> pluginConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Sections.PLUGINS);
        PluginConfig[] result = new PluginConfig[pluginConfigs.size()];
        int pos = 0;
        for (ImmutableHierarchicalConfiguration pluginConfig : pluginConfigs) {
            result[pos++] = new PluginConfigImpl(pluginConfig);
        }
        return result;
    }

    @Override
    default int getThreads() {
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
