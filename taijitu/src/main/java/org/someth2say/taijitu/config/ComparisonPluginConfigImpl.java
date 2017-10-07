package org.someth2say.taijitu.config;

public class ComparisonPluginConfigImpl implements ComparisonPluginConfig {
    
    private final ComparisonConfig comparisonConfig;
	private String name;

    public ComparisonPluginConfigImpl(final ComparisonConfig comparisonConfig, final String pluginName){
        this.comparisonConfig = comparisonConfig;
		this.name = pluginName;        
    }

	@Override
	public String getName() {
		return this.name;
	}
}
