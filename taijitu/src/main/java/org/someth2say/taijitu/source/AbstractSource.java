package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;

public abstract class AbstractSource<T> implements Source<T> {

    private final ISourceCfg iSourceCfg;

    public AbstractSource(ISourceCfg iSource) {
        iSourceCfg = iSource;
    }

    @Override
    public String getName() {
        return iSourceCfg.getName();
    }
}
