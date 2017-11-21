package org.someth2say.taijitu.compare.result;


import org.apache.commons.lang3.StringUtils;
import org.someth2say.taijitu.compare.equality.Equality;

import java.util.Collection;
import java.util.Map;

public abstract class ComparisonResult<T> {

    private final Collection<Mismatch<T>> mismatches;

    ComparisonResult(final Collection<Mismatch<T>> mismatches) {
        this.mismatches = mismatches;
    }

    public void addMismatch(Equality<T> cause, Mismatch<T> mismatch) {
        mismatches.add(mismatch);
    }

    public void addDifference(Equality<T> cause, Object id1, T composite1, Object id2, T composite2) {
        getMismatches().add(new Difference<>(cause, id1, composite1, id2, composite2));
    }

    public void addDifference(Difference<T> difference) {
        getMismatches().add(difference);
    }

    public void addDisjoint(Missing<T> missing) {
        getMismatches().add(missing);
    }

    public void addDisjoint(Equality<T> cause, Object id, T composite) {
        getMismatches().add(new Missing<>(cause, id, composite));
    }

    public void addAllDisjoint(Equality<T> cause, Map<Object, T> entries) {
        entries.forEach((id, composite) -> addDisjoint(cause, id, composite));
    }

    public Collection<Mismatch<T>> getMismatches() {
        return mismatches;
    }

    @Override
    public String toString() {
        return StringUtils.join(mismatches.toString(), ",");
    }


}
