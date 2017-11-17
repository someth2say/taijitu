package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndComposite;

import java.util.Collection;

public class Difference<DT> extends Mismatch<DT> {
    public Difference(Collection<SourceIdAndComposite<DT>> different) {
        super(different);
    }

    public Difference(SourceIdAndComposite<DT>... different) {
        super(different);
    }
}
