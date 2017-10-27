package org.someth2say.taijitu.config2.delegate;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config2.StrategyConfig;

public class DelegatedStrategyConfig extends DelegatedNamedConfig implements StrategyConfig {

    public DelegatedStrategyConfig(ImmutableHierarchicalConfiguration configuration){
        super(configuration);
    }

}
