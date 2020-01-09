package org.someth2say.taijitu.cli.source;

import java.util.Properties;

public abstract class AbstractSource<T> implements Source<T> {

    private final String name;

    public AbstractSource(final String name, final Properties buildProperties, final Properties fetchProperties){
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

}
