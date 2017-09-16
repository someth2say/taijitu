package org.someth2say.taijitu.query.objects;

import org.someth2say.taijitu.query.QueryUtilsException;

import java.sql.ResultSet;

/**
 * @author Jordi Sola
 */
public class DefaultObjectArray extends ObjectArray {

	protected DefaultObjectArray(Object[] objects) {
		super(objects);
	}

	public static class Factory implements IObjectsFactory<DefaultObjectArray> {

		public static final Factory INSTANCE = new Factory();

		@Override
		public DefaultObjectArray getInstance(Object[] values) {
			return new DefaultObjectArray(values);
		}

		@Override
		public DefaultObjectArray fromRecordSet(String[] descriptions, ResultSet rs) throws QueryUtilsException {
			return new DefaultObjectArray(ObjectsUtils.extractObjectsFromRs(descriptions, rs));
		}

	}
}
