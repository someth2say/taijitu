package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.Tuple;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    protected static ComparableTuple getNextRecord(ResultSetIterator resultSetIterator) {
        return resultSetIterator.hasNext() ? resultSetIterator.next() : null;
    }
}
