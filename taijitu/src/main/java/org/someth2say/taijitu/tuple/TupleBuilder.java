package org.someth2say.taijitu.tuple;


import java.util.function.Function;

@FunctionalInterface
public interface TupleBuilder<S> extends Function<S, ComparableTuple> {
}
