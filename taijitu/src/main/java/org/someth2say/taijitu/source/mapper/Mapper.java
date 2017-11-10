package org.someth2say.taijitu.source.mapper;

import java.util.function.Function;

import org.someth2say.taijitu.util.Named;

public interface Mapper<T1, T2> extends Function<T1, T2>, Named {}
