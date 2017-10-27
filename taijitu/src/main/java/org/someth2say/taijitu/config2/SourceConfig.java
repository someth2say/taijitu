package org.someth2say.taijitu.config2;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface SourceConfig extends Named {
    String getType();

    List<String> getKeyFields();

}
