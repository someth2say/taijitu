package org.someth2say.taijitu.compare.equality.stream;


import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;

import java.util.List;

public abstract class MismatchHelper {

    public static <T> void addMissing(List<Mismatch<T>> list, Missing<T> missing) {
        list.add(missing);
    }

    public static <T> Missing<T> addMissing(List<Mismatch<?>> list, Equality<T> cause, T composite) {
        Missing<T> missing = new Missing<>(cause, composite);
		list.add(missing);
		return missing;
    }

    public static <T> Missing<T> addMissing(List<Mismatch<?>> list, Equality<T> cause, T composite, List<Mismatch<?>> underlyingCauses) {
        Missing<T> missing = new Missing<>(cause, composite, underlyingCauses);
		list.add(missing);
		return missing;
    }

    public static <T> Difference<T> addDifference(List<Mismatch<?>> list, Equality<T> cause, T t1, T t2) {
        Difference<T> difference = new Difference<>(cause, t1, t2);
		list.add(difference);
		return difference;
    }

    public static <T> void addDifference(List<Mismatch<?>> list, Difference<T> difference) {
        list.add(difference);
    }

    public static <T> Difference<T> addDifference(List<Mismatch<?>> list, Equality<T> equality, T t1, T t2, List<Mismatch<?>> underlyingCauses) {
        Difference<T> difference = new Difference<>(equality, t1, t2, underlyingCauses);
		list.add(difference);
		return difference;
    }
}