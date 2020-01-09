package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;

public class EqualityCfg extends NamedGfg implements IEqualityCfg {

    public EqualityCfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
