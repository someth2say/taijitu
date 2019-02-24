package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Comparator;

import java.util.Date;
//Warning: DateThresholdComparator can not be a Hasher!

/**
 * A comparator of date and time instances.
 * Will identify instances as `equal` is the difference between the instance is below the defined threshold.
 * Default threshold is 1000 milisecods (1 second).
 * <p>
 * This is one of those equalizers with an special case.
 * Think about tree instance: T, T1=T+600, T2=T+1200.
 * As per equality contract, T=T1, and T1=T2. But, when using this equalizer's definition, the transitivity fails: T!=T2.
 * <p>
 * Despite of this, this equalizer is still present as have proven useful when comparing sequences of events coming from different sources.
 * Events should not only share the same information, but should also occur at the same time to be considered equal.
 * As the clocks of the sources may be slightly desynchronized, small time differences may occur, but should be acceptable.ç
 * This equalizer implements this behavior, assuming the risk of breaking absolute order in some cases.
 *
 * @param <T>
 */
public class DateThresholdComparator<T extends Date> implements Comparator<T> {

    private static final int DEFAULT_THRESHOLD = 1000;

    public static final DateThresholdComparator<Date> EQUALITY = new DateThresholdComparator<>();
    private int threshold;

    public DateThresholdComparator() {
        this(DEFAULT_THRESHOLD);
    }

    public DateThresholdComparator(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException();
        this.threshold = threshold;
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        long threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return (Math.abs(diff) < threshold);
    }

    public long getThreshold() {
        return threshold;
    }

    @Override
    public int compare(T object1, T object2) {
        long threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }


}
