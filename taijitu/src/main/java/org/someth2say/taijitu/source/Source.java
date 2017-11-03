package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.Iterator;
import java.util.List;

//TODO: Consider SourceCfg as a {@link AutoCloseable}
public interface Source extends Named, AutoCloseable {

    List<FieldDescription> getFieldDescriptions();

    Iterator<ComparableTuple> iterator();

    ISourceCfg getConfig();
}
