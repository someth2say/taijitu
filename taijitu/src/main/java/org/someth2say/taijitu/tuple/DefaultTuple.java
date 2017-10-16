package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.database.QueryUtilsException;

import java.sql.ResultSet;

/**
 * @author Jordi Sola
 */
public class DefaultTuple extends Tuple {

	protected DefaultTuple(Object[] objects) {
		super(objects);
	}

	public static class Factory implements ITupleFactory<DefaultTuple> {

		public static final Factory INSTANCE = new Factory();

		@Override
		public DefaultTuple getInstance(Object[] values) {
			return new DefaultTuple(values);
		}

		@Override
		public DefaultTuple fromRecordSet(String[] descriptions, ResultSet rs) throws QueryUtilsException {
			return new DefaultTuple(TupleUtils.extractObjectsFromRs(descriptions, rs));
		}

	}
}
