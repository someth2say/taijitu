package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.impl.defaults.IApacheNamedCfg;

abstract class NamedGfg extends Cfg implements IApacheNamedCfg {

    NamedGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }
}
