package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.INamedCfg;

abstract class NamedGfg extends Cfg implements INamedCfg {

    NamedGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }
}
