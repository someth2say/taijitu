package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.*;

public class TaijituGfg extends ComparisonCfg implements ITaijituCfg, IComparisonCfg {

    public TaijituGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
