package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.TaijituConfigDelegate;

public class TaijituConfigImpl
        extends DelegatingConfigImpl<TaijituConfigDelegate, TaijituConfigImpl>
        implements TaijituConfigDelegate {

    public TaijituConfigImpl(TaijituConfigDelegate delegate) {
        super(delegate, null);
    }

    public ComparisonConfigImpl[] getComparisons() {
        return getDelegate().getComparisons();
    }

    public Integer getThreads() {
        return getDelegate().getThreads();
    }

    public String getConsoleLog() {
        return getDelegate().getConsoleLog();
    }

    public String getFileLog() {
        return getDelegate().getFileLog();
    }

    public String getOutputFolder() {
        return getDelegate().getOutputFolder();
    }

    public Boolean isUseScanClassPath() {
        return getDelegate().isUseScanClassPath();
    }

    public PluginConfigImpl[] getComparisonPluginConfigs() {
        PluginConfigImpl[] comparisonPluginConfigs = getDelegate().getComparisonPluginConfigs();
        return comparisonPluginConfigs != null ? comparisonPluginConfigs : getParent() != null ? getParent().getComparisonPluginConfigs() : null;
    }

}
