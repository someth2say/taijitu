package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.PluginConfigDelegate;

/**
 * Right now, plugins have no configuration, only name references
 */
public class PluginConfigImpl extends DelegatingNamedConfigImpl<PluginConfigIface, PluginConfigDelegate> implements PluginConfigDelegate {

    public PluginConfigImpl(final PluginConfigIface parent, final PluginConfigIface delegate) {
        super(delegate);
    }

}
