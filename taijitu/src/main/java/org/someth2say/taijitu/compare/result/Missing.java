package org.someth2say.taijitu.compare.result;

import java.util.Map;

public class Missing<MT> extends Mismatch<MT> {
    public Missing(Map<Object, MT> entries) {
        super(entries);
    }

    public Missing(Object id, MT composite) {
        super(id, composite);
    }
}
