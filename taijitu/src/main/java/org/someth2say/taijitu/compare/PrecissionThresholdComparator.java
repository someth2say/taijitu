package org.someth2say.taijitu.compare;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author Jordi Sola
 */
public class PrecissionThresholdComparator implements Comparator<Object>, Serializable {
	private static final long serialVersionUID = 3436641942039712284L;
	private final double threshold;

    public PrecissionThresholdComparator(final double _threshold) {
        this.threshold = _threshold;
    }

	public int compare(final BigDecimal bigDec1, final BigDecimal bigGec2) {
		final double dobule1 = bigDec1.doubleValue();
		final double double2 = bigGec2.doubleValue();
		if (Math.abs(dobule1 - double2) < threshold) {
			return 0;
		} else {
			return bigDec1.compareTo(bigGec2);
		}
	}

	public int compare(final Object arg0, final Object arg1) {
		if ((arg0 instanceof BigDecimal) && (arg1 instanceof BigDecimal)) {
			return compare((BigDecimal) arg0, (BigDecimal) arg1);
		}
        throw new ClassCastException("Can't compare non BigDecimal arguments: " + arg0.getClass().getName() + ", " + arg1.getClass().getName());
    }
}