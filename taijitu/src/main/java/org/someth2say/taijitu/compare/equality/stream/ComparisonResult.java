package org.someth2say.taijitu.compare.equality.stream;


import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;

import java.util.List;

public abstract class ComparisonResult {

    public static void addMissing(List<Mismatch> list, Missing missing) {
        list.add(missing);
    }

    public static <T> void addMissing(List<Mismatch> list, Equality<T> cause, T composite) {
        list.add(new Missing<>(cause, composite));
    }

    public static <T> void addMissing(List<Mismatch> list, Equality<T> cause, T composite, List<Mismatch> underlyingCauses) {
        list.add(new Missing<>(cause, composite, underlyingCauses));
    }


    public static <T> void addDifference(List<Mismatch> list, Equality<T> cause, T t1, T t2) {
        list.add(new Difference<>(cause, t1, t2));
    }

    public static void addDifference(List<Mismatch> list, Difference difference) {
        list.add(difference);
    }

    public static <T> void addDifference(List<Mismatch> list, Equality<T> equality, T t1, T t2, List<Mismatch> underlyingCauses) {
        list.add(new Difference<>(equality, t1, t2, underlyingCauses));
    }
}
