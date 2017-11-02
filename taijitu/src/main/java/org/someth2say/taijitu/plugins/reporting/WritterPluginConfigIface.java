package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.interfaces.IPluginCfg;

public class WritterPluginConfigIface implements IPluginCfg {
    public static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}
