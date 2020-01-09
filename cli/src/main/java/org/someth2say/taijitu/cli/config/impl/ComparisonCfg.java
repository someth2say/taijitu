package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.*;

public class ComparisonCfg extends Cfg implements IComparisonCfg {

    public ComparisonCfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}

