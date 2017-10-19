package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

public interface EqualityConfig extends Named {

    String getFieldName();

    String getFieldClass();

    Object getEqualityParameters();
}
