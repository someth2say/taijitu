package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.Map;

public class Missing<MT> extends Mismatch<MT> {
    public Missing(Equality<MT> cause, Map<Object, MT> entries) {
        super(cause, entries);
    }

    public Missing(Equality<MT> cause, Object id, MT composite) {
        super(cause, id, composite);
    }
}
