package org.someth2say.taijitu.compare.equality.value;

import org.someth2say.taijitu.compare.equality.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberThresholdValueEquality implements ValueEquality<Number> {

    public static String NAME = "threshold";

    @Override
    public int computeHashCode(Number object, Object equalityConfig) {
        int scale = getScale(equalityConfig);
        double doubleValue = object.doubleValue();
        double rounded = round(doubleValue, scale);
        return Double.valueOf(rounded).hashCode();
    }

    private int getScale(Object equalityConfig) {
        return Integer.parseInt(equalityConfig.toString());
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean equals(Number object1, Number object2, Object equalityConfig) {
        int scale = getScale(equalityConfig);
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(scale);
        return (Math.abs(diff) < scaleRange);
    }

    private double getScaleRange(int scale) {
        return 1d / (10 ^ scale);
    }

    @Override
    public int compare(Number object1, Number object2, Object equalityConfig) {
        int scale = getScale(equalityConfig);
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(scale);
        return Math.abs(diff) < scaleRange ? 0 : diff < 0 ? -1 : 1;
    }

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> NumberThresholdValueEquality.NAME;
    }


    @Override
    public String getName() {
        return NAME;
    }
}
