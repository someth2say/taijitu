package org.someth2say.taijitu.config.interfaces;

public interface ITaijituCfg extends IComparisonCfg {

    IComparisonCfg[] getComparisons();

    Integer getThreads();

    String getConsoleLog();

    String getFileLog();

    String getOutputFolder();

    Boolean isUseScanClassPath();


}
