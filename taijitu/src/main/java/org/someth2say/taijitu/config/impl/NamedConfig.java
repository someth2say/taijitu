package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.util.Named;

@Deprecated
public abstract class NamedConfig implements Named {
    public final String name;

    NamedConfig(Named delegate) {
        this.name = delegate.getName();
    }

    @Override
	public String getName(){
        return this.name;
    }


}
