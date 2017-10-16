package org.someth2say.taijitu.config;

//TODO Currently there is no strategy config, but it will be in a near future.
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
