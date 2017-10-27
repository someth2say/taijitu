package org.someth2say.taijitu.config2;

import org.someth2say.taijitu.util.Named;

public interface EqualityConfig extends Named {

    String getFieldName();

    String getFieldClass();

    boolean fieldClassStrict();

    Object getEqualityParameters();
}
