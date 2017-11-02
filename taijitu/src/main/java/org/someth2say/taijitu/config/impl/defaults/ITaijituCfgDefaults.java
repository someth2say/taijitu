package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.impl.ComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ITaijituCfgDefaults<T extends ITaijituCfg> extends ITaijituCfg, ICfgDefaults<T>, IComparisonCfgDefaults<T> {

    @Override
    default List<IComparisonCfg> getComparisons() {
        List<IComparisonCfg> comparisons = getDelegate().getComparisons();
        if (comparisons != null) {
            return comparisons.stream().map(dele -> new ComparisonCfg(dele, this)).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    default Integer getThreads() {
        Integer threads = getDelegate().getThreads();
        return threads != null ? threads : DefaultConfig.DEFAULT_THREADS;
    }

    @Override
    default String getConsoleLog() {
        String consoleLog = getDelegate().getConsoleLog();
        return consoleLog != null ? consoleLog : DefaultConfig.DEFAULT_CONSOLE_LOG_LEVEL;
    }

    @Override
    default String getFileLog() {
        String fileLog = getDelegate().getFileLog();
        return fileLog != null ? fileLog : DefaultConfig.DEFAULT_FILE_LOG_LEVEL;
    }

    @Override
    default String getOutputFolder() {
        String outputFolder = getDelegate().getOutputFolder();
        return outputFolder != null ? outputFolder : DefaultConfig.DEFAULT_OUTPUT_FOLDER;
    }

    @Override
    default Boolean isUseScanClassPath() {
        Boolean useScanClassPath = getDelegate().isUseScanClassPath();
        return useScanClassPath != null ? useScanClassPath : DefaultConfig.DEFAULT_SCAN_CLASSPATH;
    }

}
