package org.someth2say.taijitu.comparison;

import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.strategy.ComparisonStrategy;

public class Comparison {
    private final ComparisonConfig   config;
    private final ComparisonStrategy strategy;

    public Comparison(ComparisonConfig config) {
        this.config = config;
        this.strategy = buildStrategy(config.getStrategyConfig());
    }


}
