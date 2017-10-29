package org.someth2say.taijitu.config.node;

import java.util.List;

public interface SourceConfigNode extends NamedNode{
    String getType();

    List<String> getKeyFields();
}
