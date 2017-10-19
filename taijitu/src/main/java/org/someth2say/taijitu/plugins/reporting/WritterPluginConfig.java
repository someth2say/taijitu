package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.PluginConfig;

public class WritterPluginConfig implements PluginConfig {
    public static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}
