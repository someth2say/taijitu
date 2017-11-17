package org.someth2say.taijitu.compare.result;


import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public abstract class ComparisonResult<T> {

    private final Collection<Mismatch<T>> mismatches;

    ComparisonResult(final Collection<Mismatch<T>> mismatches) {
        this.mismatches = mismatches;
    }

    public void addDifference(Object id1, T composite1, Object id2, T composite2) {
        getMismatches().add(new Difference<>(id1, composite1, id2, composite2));
    }

    public void addDisjoint(Object id, T composite) {
        getMismatches().add(new Missing<>(id, composite));
    }

    public void addAllDisjoint(Map<Object, T> entries) {
        entries.forEach(this::addDisjoint);
    }

    public Collection<Mismatch<T>> getMismatches() {
        return mismatches;
    }

    @Override
    public String toString() {
        return StringUtils.join(mismatches.toString(), ",");
    }


}
