package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.delegate.PluginConfigDelegate;

public class WritterPluginConfigIface implements PluginConfigDelegate {
    public static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}
