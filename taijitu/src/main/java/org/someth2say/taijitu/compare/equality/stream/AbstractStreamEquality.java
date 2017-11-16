package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;

import java.util.Iterator;

//TODO: Deprecate this class, as all equalities may come as parameters
public abstract class AbstractStreamEquality<T> implements StreamEquality<T> {

    final private Equality<T> equality;
    //TODO: Generify this to a collection of Categorizers/Comparers
    final private ComparableCategorizerEquality<T> categorizer;

    protected AbstractStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        this.equality = equality;
        this.categorizer = categorizer;
    }

    protected static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    protected Equality<T> getEquality() {
        return equality;
    }

    protected ComparableCategorizerEquality<T> getCategorizer() {
        return categorizer;
    }
}
