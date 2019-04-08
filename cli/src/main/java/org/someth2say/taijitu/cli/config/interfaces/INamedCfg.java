package org.someth2say.taijitu.cli.config.interfaces;

public interface INamedCfg extends ICfg {

    default String getName() {
        String rootValue = getConfiguration().getString("name");
        return rootValue != null ? rootValue : getConfiguration().getRootElementName();
    }

}
