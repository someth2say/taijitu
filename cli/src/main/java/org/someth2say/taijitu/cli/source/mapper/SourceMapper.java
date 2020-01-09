package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.Source;

import java.util.function.Function;

public interface SourceMapper<ORIG_TYPE, MAPPED_TYPE> extends Function<Source<ORIG_TYPE>, Source<MAPPED_TYPE>> {
}
