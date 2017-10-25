package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.config.EqualityConfig;

public class ValueThresholdEqualityStrategy implements EqualityStrategy<Number> {

    public static String NAME = "threshold";

    @Override
    public int computeHashCode(Number object, Object equalityConfig) {
        //TODO: Fail!! Should have threshold into account!
        return Double.valueOf(object.doubleValue()).hashCode();
    }

    @Override
    public boolean equals(Number object1, Number object2, Object equalityConfig) {
        Double threshold = Double.parseDouble(equalityConfig.toString());
        return (Math.abs(object1.doubleValue() - object2.doubleValue()) < threshold);
    }

    @Override
    public int compare(Number object1, Number object2, Object equalityConfig) {
        Double threshold = Double.parseDouble(equalityConfig.toString());
        double diff = object1.doubleValue() - object2.doubleValue();
        return Math.abs(diff) < threshold ? 0 : diff < 0 ? -1 : 1;
    }


    public static EqualityConfig defaultConfig() {
        return (DefaultEqualityConfig) () -> ValueThresholdEqualityStrategy.NAME;
    }


    @Override
    public String getName() {
        return NAME;
    }
}
