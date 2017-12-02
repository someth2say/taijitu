package org.someth2say.taijitu.cli.config.delegates.apache.defaults;

import org.someth2say.taijitu.cli.util.Named;

public interface ApacheNamedCfgDefaults extends ApacheCfgDefaults, Named {

    @Override
    default String getName() {
        String rootValue = getConfiguration().getString("");
        return rootValue != null ? rootValue : getConfiguration().getRootElementName();
    }

}
