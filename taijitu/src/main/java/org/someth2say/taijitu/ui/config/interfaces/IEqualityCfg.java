package org.someth2say.taijitu.ui.config.interfaces;

import org.someth2say.taijitu.util.Named;

public interface IEqualityCfg extends ICfg, Named {

    String getFieldName();

    String getFieldClass();

    Boolean isFieldClassStrict();

    Object getEqualityParameters();
}
