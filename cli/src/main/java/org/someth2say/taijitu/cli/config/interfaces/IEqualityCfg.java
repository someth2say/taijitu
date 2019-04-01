package org.someth2say.taijitu.cli.config.interfaces;

import org.someth2say.taijitu.cli.util.Named;

public interface IEqualityCfg extends ICfg, Named {

    String getFieldName();

    String getFieldClass();

    Boolean isFieldClassStrict();
}
