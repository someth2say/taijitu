package org.someth2say.taijitu.config.node;

import org.someth2say.taijitu.config.ComparisonConfig;

public interface TaijituConfigNode {
    ComparisonConfig[] getComparisons();

    int getThreads();

    String getConsoleLog();

    String getFileLog();

    String getOutputFolder();

    Boolean isUseScanClassPath();
}
