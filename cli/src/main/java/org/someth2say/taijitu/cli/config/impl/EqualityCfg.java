package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.impl.defaults.IApacheEqualityCfg;

public class EqualityCfg extends NamedGfg implements IApacheEqualityCfg {

    public EqualityCfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
