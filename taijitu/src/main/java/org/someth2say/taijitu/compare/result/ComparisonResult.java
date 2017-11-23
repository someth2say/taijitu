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

    public void addMismatch(Mismatch<T> mismatch) {
        mismatches.add(mismatch);
    }

    public void addDifference(Equality<T> cause, T composite1, T composite2) {
        getMismatches().add(new Difference<>(cause, composite1, composite2));
    }

    public void addDifference(Difference<T> difference) {
        getMismatches().add(difference);
    }

    public void addDisjoint(Missing<T> missing) {
        getMismatches().add(missing);
    }

    public void addDisjoint(Equality<T> cause, T composite) {
        getMismatches().add(new Missing<>(cause, composite));
    }

    public void addAllDisjoint(Equality<T> cause, Map<Object, T> entries) {
        entries.forEach((id, composite) -> addDisjoint(cause, composite));
    }

    public Collection<Mismatch<T>> getMismatches() {
        return mismatches;
    }

    @Override
    public String toString() {
        return StringUtils.join(mismatches.toString(), ",");
    }


}
