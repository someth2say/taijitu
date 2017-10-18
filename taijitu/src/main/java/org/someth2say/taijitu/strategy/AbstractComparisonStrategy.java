package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.tuple.Tuple;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    protected static <T extends Tuple> T getNextRecord(ResultSetIterator<T> resultSetIterator) {
        return resultSetIterator.hasNext() ? resultSetIterator.next() : null;
    }
}
