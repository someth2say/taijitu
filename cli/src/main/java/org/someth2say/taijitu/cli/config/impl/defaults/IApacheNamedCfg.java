package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.util.Named;

public interface IApacheNamedCfg extends IApacheCfg, Named {

    @Override
    default String getName() {
        String rootValue = getConfiguration().getString("name");
        return rootValue != null ? rootValue : getConfiguration().getRootElementName();
    }

}
