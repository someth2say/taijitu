package org.someth2say.taijitu.config.node;

public interface EqualityConfigNode extends NamedNode{

    String getFieldName();

    String getFieldClass();

    boolean fieldClassStrict();

    Object getEqualityParameters();

}
