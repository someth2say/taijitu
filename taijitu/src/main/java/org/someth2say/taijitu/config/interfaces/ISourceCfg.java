package org.someth2say.taijitu.config.interfaces;

import org.someth2say.taijitu.util.Named;

import java.util.List;
import java.util.Properties;

public interface ISourceCfg extends ICfg, Named {

    String getType();

    List<String> getKeyFields();

    Properties getFetchProperties();

    Properties getBuildProperties();
}
