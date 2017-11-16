package org.someth2say.taijitu.compare.equality.value;

import java.util.Date;

public class DateThreshold extends AbstractConfigurableComparableCategorizerEquality<Date> {

    private static final int DEFAULT_THRESHOLD = 1000;

    public DateThreshold(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(Date object) {
        return object.hashCode();
    }

    @Override
    public boolean equals(Date object1, Date object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return (Math.abs(diff) < threshold);
    }

    private Double getThreshold() {
        Object equalityConfig = getEqualityConfig();
        return equalityConfig != null ? Double.parseDouble(equalityConfig.toString()) : DEFAULT_THRESHOLD;
    }

    @Override
    public int compare(Date object1, Date object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }

}
