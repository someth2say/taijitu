package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

import java.sql.Timestamp;

public class TimestampThresholdValueEquality implements ValueEquality<Timestamp> {

    public static String NAME = "timestamp";

    @Override
    public int computeHashCode(Timestamp object, Object equalityConfig) {
        return 0;
    }

    @Override
    public boolean equals(Timestamp object1, Timestamp object2, Object equalityConfig) {
        Double threshold = Double.parseDouble(equalityConfig.toString());
        return (Math.abs(object1.getTime() - object2.getTime()) < threshold);
    }

    @Override
    public int compare(Timestamp object1, Timestamp object2, Object equalityConfig) {
        Double threshold = Double.parseDouble(equalityConfig.toString());
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
