package org.someth2say.taijitu.source;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.Iterator;

public interface Source<T> {

    FieldDescription[] getFieldDescriptions();

    Iterator<T> iterator();
}
