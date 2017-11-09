package org.someth2say.taijitu.tuple;


import java.util.List;
import java.util.function.BiFunction;

@FunctionalInterface
public interface TupleBuilder<S> extends BiFunction<S, List<FieldDescription>, Tuple> {
}
