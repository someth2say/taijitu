package org.someth2say.taijitu.tuple;


import java.util.function.Function;

@FunctionalInterface
public interface TupleBuilder<S, T extends Tuple> extends Function<S, T> {
}
