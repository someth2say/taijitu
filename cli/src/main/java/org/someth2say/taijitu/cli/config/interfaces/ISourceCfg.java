package org.someth2say.taijitu.cli.config.interfaces;

import org.someth2say.taijitu.cli.util.Named;

import java.util.Properties;

public interface ISourceCfg extends ICfg, Named {

    String getType();

    Properties getFetchProperties();

    Properties getBuildProperties();

	String getMapper();
}
