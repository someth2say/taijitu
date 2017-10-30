package org.someth2say.taijitu.config.impl;


import org.someth2say.taijitu.config.delegate.SourceConfigDelegate;
import org.someth2say.taijitu.config.delegate.StrategyConfigDelegate;

public class StrategyConfigImpl extends DelegatingNamedConfigImpl<StrategyConfigIface, SourceConfigDelegate> implements StrategyConfigDelegate {
    public StrategyConfigImpl(StrategyConfigIface parent, SourceConfigDelegate delegate) {
        super(delegate);
    }
}
