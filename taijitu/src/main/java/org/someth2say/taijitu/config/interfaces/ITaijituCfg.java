package org.someth2say.taijitu.config.interfaces;

import java.util.List;

public interface ITaijituCfg extends IComparisonCfg {

    List<IComparisonCfg> getComparisons();

    Integer getThreads();

    Boolean isUseScanClassPath();


}
