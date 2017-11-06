package org.someth2say.taijitu.strategy;

import java.util.Iterator;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    protected static <T> T getNextRecord(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
