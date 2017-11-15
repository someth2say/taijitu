package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;

public abstract class AbstractSource<T> implements Source<T> {

    private final String name;

    public AbstractSource(final ISourceCfg sourceCfg) {
        this.name = sourceCfg.getName();
    }

    public AbstractSource(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
