package org.someth2say.taijitu.config.impl.defaults;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;

import java.util.List;
import java.util.Properties;

public interface ISourceCfgDefaults<T extends ISourceCfg> extends ISourceCfg, ICfgDefaults<T> {
    @Override
    default String getType() {
        return getDelegate().getType();
    }


    @Override
    default List<String> getKeyFields() {
        List<String> keyFields = getDelegate().getKeyFields();
        return keyFields != null ? keyFields : getParent() != null ? getParent().getKeyFields() : null;
    }

    @Override
    default Properties getFetchProperties() {
        Properties sourceProperties = getDelegate().getFetchProperties();
        return sourceProperties != null ? sourceProperties : getParent() != null ? getParent().getFetchProperties() : null;
    }

    @Override
    default Properties getBuildProperties() {
        Properties sourceProperties = getDelegate().getBuildProperties();
        return sourceProperties != null ? sourceProperties : getParent() != null ? getParent().getBuildProperties() : null;
    }
}
