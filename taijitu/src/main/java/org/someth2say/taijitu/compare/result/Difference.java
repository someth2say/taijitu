package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.Map;

public class Difference<DT> extends Mismatch<DT> {

    public Difference(Equality<DT> cause, Map<Object, DT> entries) {
        super(cause, entries);
    }

    public Difference(Equality<DT> cause, Object id, DT composite) {
        super(cause, id, composite);
    }

    public Difference(Equality<DT> cause, Object id, DT composite, Object id2, DT composite2) {
        super(cause, id, composite, id2, composite2);
    }
}
