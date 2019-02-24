package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberThresholdComparatorHasher<T extends Number> implements ComparatorHasher<T> {

    private static final int DEFAULT_SCALE = 2;

    public static final NumberThresholdComparatorHasher<Number> INSTANCE = new NumberThresholdComparatorHasher<>();
    private int scale;

    public NumberThresholdComparatorHasher() {
        this(DEFAULT_SCALE);
    }

    public NumberThresholdComparatorHasher(int scale) {
        if (scale < 0) throw new IllegalArgumentException();
        this.scale = scale;
    }

    @Override
    public int hash(T object) {
        double doubleValue = object.doubleValue();
        double rounded = round(doubleValue);
        return Double.valueOf(rounded).hashCode();
    }

    public int getScale() {
        return scale;
    }

    private double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(getScale(), RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean areEquals(T object1, T object2) {
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(getScale());
        return (Math.abs(diff) < scaleRange);
    }

    private double getScaleRange(int scale) {
        return 1 / Math.pow(10, scale);
    }

    @Override
    public int compare(T object1, T object2) {
        double diff = object1.doubleValue() - object2.doubleValue();
        double scaleRange = getScaleRange(getScale());
        return Math.abs(diff) < scaleRange ? 0 : diff < 0 ? -1 : 1;
    }
}
