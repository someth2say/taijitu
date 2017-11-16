package org.someth2say.taijitu.ui.plugins.reporting;

import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;

public class WritterPluginConfigIface implements IPluginCfg {
    private static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}
