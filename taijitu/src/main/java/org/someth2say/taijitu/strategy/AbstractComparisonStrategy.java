package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.tuple.ComparableTuple;

import java.util.Iterator;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    protected static ComparableTuple getNextRecord(Iterator<ComparableTuple> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
