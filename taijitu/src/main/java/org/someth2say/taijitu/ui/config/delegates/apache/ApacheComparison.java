package org.someth2say.taijitu.ui.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.ui.config.delegates.apache.defaults.ApacheComparisonCfgDefaults;

public class ApacheComparison extends Apache<ApacheComparisonCfgDefaults> implements ApacheComparisonCfgDefaults {

    public ApacheComparison(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

}

