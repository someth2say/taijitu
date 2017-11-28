package org.someth2say.taijitu.ui.source.mapper;

import java.util.function.Function;

import org.someth2say.taijitu.ui.source.Source;
import org.someth2say.taijitu.util.Named;

public interface SourceMapper<T1, T2> extends Function<Source<T1>, Source<T2>>, Named {
    Class<T2> getTypeParameter();
}
