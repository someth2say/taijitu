package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.util.Iterator;
import java.util.List;

public interface Source {

    List<FieldDescription> getFieldDescriptions();

    Iterator<ComparableTuple> iterator();

    TupleBuilder getTupleBuilder();

    QueryConfig getConfig();
}
