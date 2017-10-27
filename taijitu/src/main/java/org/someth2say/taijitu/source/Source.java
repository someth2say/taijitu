package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.Iterator;
import java.util.List;

//TODO: Consider Source as a {@link AutoCloseable}
public interface Source extends Named {

    List<FieldDescription> getFieldDescriptions();

    Iterator<ComparableTuple> iterator();

    SourceConfig getConfig();
}
