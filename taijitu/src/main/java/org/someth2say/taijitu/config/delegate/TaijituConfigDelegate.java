package org.someth2say.taijitu.config.delegate;

import org.someth2say.taijitu.config.impl.ComparisonConfigImpl;
import org.someth2say.taijitu.config.impl.PluginConfigImpl;

public interface TaijituConfigDelegate extends ConfigDelegate {

    ComparisonConfigImpl[] getComparisons();

    Integer getThreads();

    String getConsoleLog();

    String getFileLog();

    String getOutputFolder();

    Boolean isUseScanClassPath();

    PluginConfigImpl[] getComparisonPluginConfigs();

}
