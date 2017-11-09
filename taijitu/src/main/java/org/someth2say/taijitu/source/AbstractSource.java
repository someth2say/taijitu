package org.someth2say.taijitu.source;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.matcher.FieldMatcher;

public abstract class AbstractSource<T> implements Source<T> {
    protected final IComparisonCfg iComparisonCfg;
    protected final ComparisonContext context;
    private final FieldMatcher matcher;
    protected final ISourceCfg iSource;

    public AbstractSource(final ISourceCfg iSource, final IComparisonCfg iComparisonCfg, final ComparisonContext context, FieldMatcher matcher) {
        this.iComparisonCfg = iComparisonCfg;
        this.iSource = iSource;
        this.context = context;
        this.matcher = matcher;
    }

    public FieldMatcher getMatcher() {
        return matcher;
    }

    @Override
    public ISourceCfg getConfig() {
        return iSource;
    }

}
