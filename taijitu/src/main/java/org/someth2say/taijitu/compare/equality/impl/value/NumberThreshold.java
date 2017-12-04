package org.someth2say.taijitu.compare.equality.impl.value;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberThreshold<T extends Number> extends AbstractConfigurableEqualizer<T> implements ComparatorHasherEqualizer<T> {

    private static final int DEFAULT_SCALE = 2;

    public NumberThreshold() {
        this(null);
    }

    public NumberThreshold(Object equalityConfig) {
        super(equalityConfig);
    }

    @Override
    public int hashCode(T object) {
        int scale = getScale();
        double doubleValue = object.doubleValue();
        double rounded = round(doubleValue, scale);
        return Double.valueOf(rounded).hashCode();
    }

    private int getScale() {
        Object equalityConfig = getEqualityConfig();
        return equalityConfig != null ? Integer.parseInt(equalityConfig.toString()) : DEFAULT_SCALE;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean equals(T object1, T object2) {
        int scale = getScale();
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(scale);
        return (Math.abs(diff) < scaleRange);
    }

    private double getScaleRange(int scale) {
        return 1d / (10 ^ scale);
    }

    @Override
    public int compare(T object1, T object2) {
        int scale = getScale();
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(scale);
        return Math.abs(diff) < scaleRange ? 0 : diff < 0 ? -1 : 1;
    }
}