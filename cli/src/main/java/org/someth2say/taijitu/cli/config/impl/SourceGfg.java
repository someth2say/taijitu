package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.impl.defaults.IApacheSourceCfg;

public class SourceGfg extends NamedGfg implements IApacheSourceCfg {

    public SourceGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
