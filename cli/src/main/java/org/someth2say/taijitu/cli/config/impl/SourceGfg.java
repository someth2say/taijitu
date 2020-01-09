package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

public class SourceGfg extends NamedGfg implements ISourceCfg {

    public SourceGfg(HierarchicalConfiguration configuration) {
        super(configuration);
    }

}
