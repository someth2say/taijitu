package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.config.interfaces.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

import java.sql.Timestamp;

public class TimestampThresholdValueEquality extends AbstractComparableValueEquality<Timestamp> {

    public static String NAME = "timestamp";
    public static final int DEFAULT_THRESHOLD = 1000;

    public TimestampThresholdValueEquality(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int computeHashCode(Timestamp object) {
        //TODO
        return 0;
    }

    @Override
    public boolean equals(Timestamp object1, Timestamp object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return (Math.abs(diff) < threshold);
    }

    private Double getThreshold() {
        Object equalityConfig = getEqualityConfig();
        return equalityConfig!=null?Double.parseDouble(equalityConfig.toString()): DEFAULT_THRESHOLD;
    }

    @Override
    public int compare(Timestamp object1, Timestamp object2) {
        Double threshold = getThreshold();
        long diff = object1.getTime() - object2.getTime();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> TimestampThresholdValueEquality.NAME;
    }


    @Override
    public String getName() {
        return NAME;
    }
}
