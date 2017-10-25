package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.EqualityConfig;
import java.sql.Timestamp;

public class TimestampThresholdEqualityStrategy implements EqualityStrategy<Timestamp> {

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


    public static EqualityConfig defaultConfig() {
        return new EqualityConfig() {
            @Override
            public String getFieldName() {
                return null;
            }

            @Override
            public String getFieldClass() {
                return null;
            }

            @Override
            public boolean fieldClassStrict() {
                return false;
            }

            @Override
            public Object getEqualityParameters() {
                return null;
            }

            @Override
            public String getName() {
                return TimestampThresholdEqualityStrategy.NAME;
            }
        };
    }


    @Override
    public String getName() {
        return NAME;
    }
}
