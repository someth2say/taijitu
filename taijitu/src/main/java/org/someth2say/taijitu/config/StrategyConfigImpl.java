package org.someth2say.taijitu.config;

public class StrategyConfigImpl implements StrategyConfig {
    
    private ComparisonConfig comparisonConfig;

    public StrategyConfigImpl(ComparisonConfigImpl comparisonConfig) {

        this.comparisonConfig = comparisonConfig;
        
    }
}
