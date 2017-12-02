package org.someth2say.taijitu.cli.config.delegates.simple;

import org.someth2say.taijitu.cli.config.interfaces.IStrategyCfg;

public class BasicStrategyCfg implements IStrategyCfg {

    private final String name;

    public BasicStrategyCfg(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
