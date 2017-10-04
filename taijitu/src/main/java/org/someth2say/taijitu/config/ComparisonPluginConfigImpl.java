package org.someth2say.taijitu.config;

public class ComparisonPluginConfigImpl implements ComparisonPluginConfig {
    
    private final ComparisonConfig comparisonConfig;

    public ComparisonPluginConfigImpl(final ComparisonConfig comparisonConfig, final String pluginName){
        this.comparisonConfig = comparisonConfig;        
    }
}
