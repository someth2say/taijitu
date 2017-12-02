package org.someth2say.taijitu.cli.config.delegates.simple;

import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;

public class BasicPluginCfg implements IPluginCfg {

    private final String name;

    public BasicPluginCfg(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
