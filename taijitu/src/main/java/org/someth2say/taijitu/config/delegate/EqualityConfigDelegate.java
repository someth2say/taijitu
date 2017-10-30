package org.someth2say.taijitu.config.delegate;

import org.someth2say.taijitu.util.Named;

public interface EqualityConfigDelegate extends ConfigDelegate, Named {

    String getFieldName();

    String getFieldClass();

    Boolean isFieldClassStrict();

    Object getEqualityParameters();
}
