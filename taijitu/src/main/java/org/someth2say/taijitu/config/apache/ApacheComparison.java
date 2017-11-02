package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.apache.defaults.ApacheComparisonConfig;

public class ApacheComparison extends Apache<ApacheComparisonConfig> implements ApacheComparisonConfig {

    public ApacheComparison(ImmutableHierarchicalConfiguration configuration, ApacheComparisonConfig parent) {
        super(configuration, parent);
    }

}

