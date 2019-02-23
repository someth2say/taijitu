package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Comparator;

import java.util.Date;
//Warning: DateThreshold can not be a Hasher!

/**
 * A comparator of date and time instances.
 * Will identify instances as `equal` is the difference between the instance is below the defined threshold.
 * Default threshold is 1000 milisecods (1 second).
 *
 * This is one of those equalizers with an special case.
 * Think about tree instance: T, T1=T+600, T2=T+1200.
 * As per equality contract, T=T1, and T1=T2. But, when using this equalizer's definition, the transitivity fails: T!=T2.
 *
 * Despite of this, this equalizer is still present as have proven useful when comparing sequences of events coming from different sources.
 * Events should not only share the same information, but should also occur at the same time to be considered equal.
 * As the clocks of the sources may be slightly desynchronized, small time differences may occur, but should be acceptable.ç
 * This equalizer implements this behavior, assuming the risk of breaking absolute order in some cases.
 * @param <T>
 */
public class DateThreshold<T extends Date> extends AbstractConfigurableEqualizer<T> implements Comparator<T> {

    private static final int DEFAULT_THRESHOLD = 1000;

    public static final DateThreshold<Date> EQUALITY = new DateThreshold<>();

    public DateThreshold() {
        this(null);
    }

    public DateThreshold(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        long threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return (Math.abs(diff) < threshold);
    }

    private long getThreshold() {
        Object equalityConfig = getEqualityConfig();
        return equalityConfig != null ? Long.parseLong(equalityConfig.toString()) : DEFAULT_THRESHOLD;
    }

    @Override
    public int compare(T object1, T object2) {
        long threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }



}
