package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndComposite;

import java.util.Collection;

public class Missing<MT> extends Mismatch<MT> {
    public Missing(Collection<SourceIdAndComposite<MT>> existing) {
        super(existing);
    }

    public Missing(SourceIdAndComposite<MT>... different) {
        super(different);
    }
}
