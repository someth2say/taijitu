package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;

import java.util.Date;

public class DateThreshold<T extends Date> extends AbstractConfigurableEqualizer<T> implements Comparator<T> {

    private static final int DEFAULT_THRESHOLD = 1000;

    public DateThreshold() {
        this(null);
    }

    public DateThreshold(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public boolean equals(T object1, T object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return (Math.abs(diff) < threshold);
    }

    private Double getThreshold() {
        Object equalityConfig = getEqualityConfig();
        return equalityConfig != null ? Double.parseDouble(equalityConfig.toString()) : DEFAULT_THRESHOLD;
    }

    @Override
    public int compare(T object1, T object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }

}
