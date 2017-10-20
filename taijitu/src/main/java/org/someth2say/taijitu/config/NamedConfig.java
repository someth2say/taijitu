package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

public abstract class NamedConfig implements Named {
    public final String name;

    protected NamedConfig(String name) {
        this.name = name;
    }

    @Override
	public String getName(){
        return this.name;
    }


}
