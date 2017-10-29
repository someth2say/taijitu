package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.FileSourceConfig;

public class ApacheFileSourceConfigNode extends ApacheSourceConfigNode implements FileSourceConfig{
    protected ApacheFileSourceConfigNode(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getPath() {
        return null;
    }
}
