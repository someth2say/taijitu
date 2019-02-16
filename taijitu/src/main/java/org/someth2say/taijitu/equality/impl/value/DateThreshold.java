package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.Comparator;

import java.util.Date;
//Warning: DateThreshold can not be a Hasher!
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
