package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.database.QueryUtilsException;

import java.sql.ResultSet;

/**
 * @author Jordi Sola
 */
public interface ITupleFactory<T extends Tuple> {
    T getInstance(Object[] values);

    T fromRecordSet(String[] columnNames, ResultSet rs) throws QueryUtilsException;

}
