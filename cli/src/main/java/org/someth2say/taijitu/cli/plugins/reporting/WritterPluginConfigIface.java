package org.someth2say.taijitu.cli.plugins.reporting;

import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;

public class WritterPluginConfigIface implements IPluginCfg {
    private static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}