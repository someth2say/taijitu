package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.impl.defaults.IApacheComparisonCfg;

public class ComparisonCfg extends Cfg implements IApacheComparisonCfg {

    public ComparisonCfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}

