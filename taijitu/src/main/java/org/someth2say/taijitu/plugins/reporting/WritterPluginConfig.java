package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.ComparisonPluginConfig;

public class WritterPluginConfig implements ComparisonPluginConfig {
    public static final String NAME = "writter";

    @Override
    public String getName() {
        return NAME;
    }
}
