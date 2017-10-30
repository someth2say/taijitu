package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.DatabaseConfigDelegate;
import org.someth2say.taijitu.config.delegating.DatabaseConfigIface;

public class DatabaseConfigImpl extends DelegatingConfigImpl<DatabaseConfigDelegate> implements DatabaseConfigIface {

    public DatabaseConfigImpl(DatabaseConfigIface parent, DatabaseConfigDelegate delegate) {
        super(delegate);
    }
}
