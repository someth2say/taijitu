package org.someth2say.taijitu.ui.config.impl.defaults;

import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.impl.ComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.ITaijituCfg;

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
    default Boolean isUseScanClassPath() {
        Boolean useScanClassPath = getDelegate().isUseScanClassPath();
        return useScanClassPath != null ? useScanClassPath : DefaultConfig.DEFAULT_SCAN_CLASSPATH;
    }

}
