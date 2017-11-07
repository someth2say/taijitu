package org.someth2say.taijitu.source;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;

public abstract class AbstractSource implements Source {
    protected final IComparisonCfg iComparisonCfg;
    protected final ComparisonContext context;
    protected final ISourceCfg iSource;

    public AbstractSource(final ISourceCfg iSource, final IComparisonCfg iComparisonCfg, final ComparisonContext context) {
        this.iComparisonCfg = iComparisonCfg;
        this.iSource = iSource;
        this.context = context;
    }

    @Override
    public ISourceCfg getConfig() {
        return iSource;
    }
}
