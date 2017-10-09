package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.tuple.Tuple;
import org.someth2say.taijitu.query.tuple.ITupleFactory;
import org.someth2say.taijitu.query.tuple.TupleUtils;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Jordi Sola
 */
public class ComparableTuple extends Tuple {

	private ComparableTuple(Object[] values) {
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
				// Use default equalsFields
				return value.equals(otherValue);
			}
		} else {
			return value == null && otherValue == null;
		}
	}

	// TODO: This comparison assumes both tuple have the same description (I.O.W.,
	// the compare fields for both are the same). This should be amended.
	public boolean equalsFields(ComparableTuple other, Map<Class<?>, Comparator<Object>> comparators,
								int[] compareFieldsIdx) {
		for (int fieldIdx : compareFieldsIdx) {
			if (!equalsColumnValue(getValue(fieldIdx), other.getValue(fieldIdx), comparators)) {
				return false;
			}
		}
		return true;
	}

	public int compareFields(ComparableTuple other, Map<Class<?>, Comparator<Object>> comparators,
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

	public boolean isColumnEquals(ComparableTuple other, int columnIdx,
								  Map<Class<?>, Comparator<Object>> comparators) {
		return equalsColumnValue(getValue(columnIdx), other.getValue(columnIdx), comparators);
	}

	public static class Factory implements ITupleFactory<ComparableTuple> {

		public static final Factory INSTANCE = new Factory();

		@Override
		public ComparableTuple getInstance(Object[] values) {
			return new ComparableTuple(values);
		}

		@Override
		public ComparableTuple fromRecordSet(String[] descriptions, ResultSet rs) throws QueryUtilsException {
			return new ComparableTuple(TupleUtils.extractObjectsFromRs(descriptions, rs));
		}
	}

}
