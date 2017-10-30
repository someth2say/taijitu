package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.util.Named;

public abstract class ApacheNamedConfig extends ApacheBasedConfig implements Named {
    ApacheNamedConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return getConfiguration().getRootElementName();
    }
}
