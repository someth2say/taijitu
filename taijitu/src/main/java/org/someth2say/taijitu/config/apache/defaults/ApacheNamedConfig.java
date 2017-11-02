package org.someth2say.taijitu.config.apache.defaults;

import org.someth2say.taijitu.util.Named;

public interface ApacheNamedConfig extends ApacheConfig, Named {

    @Override
    default String getName() {
        String rootValue = getConfiguration().getString("");
        return rootValue != null ? rootValue : getConfiguration().getRootElementName();
    }

}
