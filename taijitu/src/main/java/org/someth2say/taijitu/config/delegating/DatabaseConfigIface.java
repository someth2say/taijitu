package org.someth2say.taijitu.config.delegating;

import org.someth2say.taijitu.config.delegate.DatabaseConfigDelegate;

import java.util.Properties;

public interface DatabaseConfigIface extends DelegatingConfigIface<DatabaseConfigDelegate>, DatabaseConfigDelegate {
    default Properties getDatabaseProperties() {
        Properties databaseProperties = getDelegate().getDatabaseProperties();
        return databaseProperties != null ? databaseProperties : getParent() != null ? getParent().getDatabaseProperties() : null;
    }
}
