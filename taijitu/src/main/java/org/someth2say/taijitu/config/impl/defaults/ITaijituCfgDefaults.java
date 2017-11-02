package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.impl.ComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface ITaijituCfgDefaults<T extends ITaijituCfg> extends ITaijituCfg, ICfgDefaults<T>, IComparisonCfgDefaults<T> {

    @Override
    default IComparisonCfg[] getComparisons() {
        return Arrays.stream(getDelegate().getComparisons()).map(dele -> new ComparisonCfg(dele, this)).collect(Collectors.toList()).toArray(new IComparisonCfg[0]);
    }

    @Override
    default Integer getThreads() {
        return getDelegate().getThreads();
    }

    @Override
    default String getConsoleLog() {
        return getDelegate().getConsoleLog();
    }

    @Override
    default String getFileLog() {
        return getDelegate().getFileLog();
    }

    @Override
    default String getOutputFolder() {
        return getDelegate().getOutputFolder();
    }

    @Override
    default Boolean isUseScanClassPath() {
        return getDelegate().isUseScanClassPath();
    }

}
