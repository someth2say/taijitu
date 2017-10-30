package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.EqualityConfigDelegate;
import org.someth2say.taijitu.config.delegating.EqualityConfigIface;

public class EqualityConfigImpl extends DelegatingNamedConfigImpl<EqualityConfigIface, EqualityConfigDelegate> implements EqualityConfigIface {

    public EqualityConfigImpl(EqualityConfigDelegate delegate) {
        super(delegate);
    }

}
