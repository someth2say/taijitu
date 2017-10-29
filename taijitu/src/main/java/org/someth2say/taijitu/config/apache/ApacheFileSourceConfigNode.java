package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.FileSourceConfig;

public class ApacheFileSourceConfigNode extends ApacheSourceConfigNode implements FileSourceConfig {
    ApacheFileSourceConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheComparisonConfigNode parent) {
        super(configuration, parent);
    }

    @Override
    public String getPath() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.FILE_PATH);
    }
}
