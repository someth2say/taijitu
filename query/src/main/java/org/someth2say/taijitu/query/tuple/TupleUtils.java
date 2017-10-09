package org.someth2say.taijitu.query.tuple;

import org.someth2say.taijitu.query.QueryUtilsException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jordi Sola on 17/02/2017.
 */
public class TupleUtils {
    private TupleUtils() {
    }

    public static Object[] extractObjectsFromRs(String[] descriptions, ResultSet rs) throws QueryUtilsException {
        Object[] columnValues = new Object[descriptions.length];
        for (int columnIdx = 0, descriptionsLength = descriptions.length; columnIdx < descriptionsLength; columnIdx++) {
            try {
                columnValues[columnIdx] = rs.getObject(columnIdx + 1);
            } catch (SQLException e) {
                throw new QueryUtilsException("Can\'t retrieve column value for " + descriptions[columnIdx], e);
            }
        }
        return columnValues;
    }
}
