package org.someth2say.taijitu.query.objects;

import org.someth2say.taijitu.query.QueryUtilsException;

import java.sql.ResultSet;

/**
 * @author Jordi Sola
 */
public interface IObjectsFactory<T extends ObjectArray> {
    T getInstance(Object[] values);

    T fromRecordSet(String[] columnNames, ResultSet rs) throws QueryUtilsException;

}
