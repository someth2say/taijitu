package org.someth2say.taijitu.query.querywalker;

import org.someth2say.taijitu.query.tuple.Tuple;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jordi Sola on 20/02/2017.
 */
@Deprecated
public class MemStoreResults<T extends Tuple> {
    private String[] descriptions;
    private Collection<T> values;

    public MemStoreResults(String[] columnDescriptions, Collection<T> values) {
        this.descriptions = columnDescriptions;
        this.values = values;
    }

    public MemStoreResults() {
        this.values = new ArrayList<>();
    }


    public String[] getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
    }

    public Collection<T> getValues() {
        return values;
    }

    public void setValues(Collection<T> values) {
        this.values = values;
    }
}
