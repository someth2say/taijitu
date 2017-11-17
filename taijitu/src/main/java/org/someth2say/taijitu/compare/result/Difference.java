package org.someth2say.taijitu.compare.result;

import java.util.Map;

public class Difference<DT> extends Mismatch<DT> {

    public Difference(Map<Object, DT> entries) {
        super(entries);
    }

    public Difference(Object id, DT composite, Object id2, DT composite2) {
        super(id, composite, id2, composite2);
    }
}
