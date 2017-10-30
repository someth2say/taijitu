package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.SourceConfigDelegate;

import java.util.List;
import java.util.Properties;

public class SourceConfigImpl<P extends org.someth2say.taijitu.config.delegating.DelegatingConfigIface & SourceConfigDelegate, D extends SourceConfigDelegate> extends DelegatingNamedConfigImpl<P, D> implements org.someth2say.taijitu.config.delegating.DelegatingConfigIface<D>,SourceConfigDelegate, org.someth2say.taijitu.config.delegating.DelegatingConfigIface<D> {
    public SourceConfigImpl(D delegate) {
        super(delegate);
    }

    public String getType() {
        return getDelegate().getType();
    }

    public List<String> getKeyFields() {
        List<String> keyFields = getDelegate().getKeyFields();
        return keyFields != null ? keyFields : getParent() != null ? getParent().getKeyFields() : null;
    }

    public Properties getSourceProperties(){
        Properties sourceProperties = getDelegate().getSourceProperties();
        return sourceProperties != null ? sourceProperties : getParent() != null ? getParent().getSourceProperties() : null;

    }
}
