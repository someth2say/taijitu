package org.someth2say.taijitu.query.queryactions;

import org.someth2say.taijitu.query.tuple.Tuple;

/**
 * @author Jordi Sola
 */
//TODO: Move this to a stream (maybe use JOOQ Streams?
@Deprecated
public interface QueryActions<T extends Tuple> {
    void start(String[] columnDescriptions) throws QueryActionsException;

    void step(T var1) throws QueryActionsException;

    void end() throws QueryActionsException;
}
