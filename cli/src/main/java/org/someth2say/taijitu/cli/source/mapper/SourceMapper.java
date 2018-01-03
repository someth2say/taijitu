package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.Source;

import java.util.function.Function;

public interface SourceMapper<T1, T2> extends Function<Source<T1>, Source<T2>> {
    Class<T2> getTypeParameter();
}
