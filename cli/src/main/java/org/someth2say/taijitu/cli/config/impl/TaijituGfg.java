package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.impl.defaults.IApacheTaijituCfg;

public class TaijituGfg extends ComparisonCfg implements IApacheTaijituCfg {

    public TaijituGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
