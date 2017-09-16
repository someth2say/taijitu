package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.objects.ObjectArray;
import org.someth2say.taijitu.query.objects.IObjectsFactory;
import org.someth2say.taijitu.query.objects.ObjectsUtils;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Jordi Sola
 */
public class ComparableObjectArray extends ObjectArray {

	private ComparableObjectArray(Object[] values) {
		super(values);
	}

	public static int compareColumnValue(Object value, Object otherValue,
			Map<Class<?>, Comparator<Object>> comparators) {
		if (value != null && otherValue != null) {
			final Class<?> valueClass = value.getClass();
			final Comparator<Object> comparator = comparators.get(valueClass);
			if (comparator != null) {
				// may use provided comparator
				return comparator.compare(value, otherValue);
			} else {
				// Compare the string representation
				return value.toString().compareTo(otherValue.toString());
			}
		} else if (value == null && otherValue != null) {
			return -1;
		} else if (value != null) {// && otherValue == null)
			return 1;
		}
		return 0;
	}

	private boolean equalsColumnValue(Object value, Object otherValue, Map<Class<?>, Comparator<Object>> comparators) {
		if (value != null && otherValue != null) {
			final Class<?> valueClass = value.getClass();
			final Comparator<Object> comparator = comparators.get(valueClass);
			if (comparator != null) {
				// may use provided comparator
				return comparator.compare(value, otherValue) == 0;
			} else {
				// Use default equalsCompareFields
				return value.equals(otherValue);
			}
		} else {
			return value == null && otherValue == null;
		}
	}

	// TODO: This comparison assumes both objects have the same description (I.O.W.,
	// the compare fields for both are the same). This should be amended.
	public boolean equalsCompareFields(ComparableObjectArray other, Map<Class<?>, Comparator<Object>> comparators,
			int[] compareFieldsIdx) {
		for (int fieldIdx : compareFieldsIdx) {
			if (!equalsColumnValue(getValue(fieldIdx), other.getValue(fieldIdx), comparators)) {
				return false;
			}
		}
		return true;
	}

	public int compareFields(ComparableObjectArray other, Map<Class<?>, Comparator<Object>> comparators,
			int[] compareFieldsIdx) {
		for (int objIdx : compareFieldsIdx) {
			final int fieldComparisonValue = compareColumnValue(getValue(objIdx), other.getValue(objIdx), comparators);
			if (fieldComparisonValue != 0) {
				return fieldComparisonValue;
			}
		}
		return 0;
	}

	public String[] toStringArray() {
		String[] result = new String[size()];
		int pos = 0;

		while (pos < this.size()) {
			result[pos++] = this.getValue(pos).toString();
		}

		return result;
	}

	public boolean isColumnEquals(ComparableObjectArray other, int columnIdx,
			Map<Class<?>, Comparator<Object>> comparators) {
		return equalsColumnValue(getValue(columnIdx), other.getValue(columnIdx), comparators);
	}

	public static class Factory implements IObjectsFactory<ComparableObjectArray> {

		public static final Factory INSTANCE = new Factory();

		@Override
		public ComparableObjectArray getInstance(Object[] values) {
			return new ComparableObjectArray(values);
		}

		@Override
		public ComparableObjectArray fromRecordSet(String[] descriptions, ResultSet rs) throws QueryUtilsException {
			return new ComparableObjectArray(ObjectsUtils.extractObjectsFromRs(descriptions, rs));
		}
	}

}
