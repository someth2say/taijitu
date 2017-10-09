package org.someth2say.taijitu.query.queryactions;

import org.someth2say.taijitu.query.tuple.Tuple;
import org.someth2say.taijitu.query.querywalker.MemStoreResults;

import java.util.Collection;

/**
 * @author Jordi Sola
 */
public class MemStoreQueryActions<T extends Tuple> implements QueryActions<T> {
    private final MemStoreResults<T> memStore;

    public MemStoreQueryActions() {
        memStore = new MemStoreResults<>();
    }

    public MemStoreQueryActions(MemStoreResults<T> memStore) {
        this.memStore = memStore;
    }

    @Override
    public void start(String[] columnDescriptions) throws QueryActionsException {
        this.memStore.setDescriptions(columnDescriptions);
    }

    @Override
    public void step(T currentRecord) throws QueryActionsException {
        this.getValues().add(currentRecord);
    }

    @Override
    public void end() throws QueryActionsException {
        // Nothing to do
    }

    public Collection<T> getValues() {
        return this.memStore.getValues();
    }

    public String[] getColumnDescriptions() {
        return this.memStore.getDescriptions();
    }
}
