package org.someth2say.taijitu.query.queryactions;

import org.someth2say.taijitu.query.objects.ObjectArray;

/**
 * @author Jordi Sola
 */
public interface QueryActions<T extends ObjectArray> {
    void start(String[] columnDescriptions) throws QueryActionsException;

    void step(T var1) throws QueryActionsException;

    void end() throws QueryActionsException;
}
