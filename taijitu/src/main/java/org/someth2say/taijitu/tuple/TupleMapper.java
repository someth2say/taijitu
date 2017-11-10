package org.someth2say.taijitu.tuple;


import java.util.function.Function;

@FunctionalInterface
public interface TupleMapper<S> extends Function<S, Tuple> {
}
