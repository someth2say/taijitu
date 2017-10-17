package org.someth2say.taijitu.config.old;

import org.someth2say.taijitu.config.StrategyConfig;

//TODO Currently there is no strategy configuration, but it will be in a near future.
@Deprecated
public class StrategyConfigImpl implements StrategyConfig {

    private final String name;

    public StrategyConfigImpl(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;

    }
}
