package org.someth2say.taijitu.query.columnDescription;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.tuple.Tuple;
import org.someth2say.taijitu.commons.StringUtil;

import java.util.Arrays;

/**
 * Created by Jordi Sola on 20/02/2017.
 */
public class ColumnDescriptionUtils {
    private static final Logger logger = Logger.getLogger(ColumnDescriptionUtils.class);

    private ColumnDescriptionUtils() {
    }

    public static int[] getFieldPositions(String[] fields, String[] columnNames) {
        int[] result = new int[fields.length];

        for (int fieldIdx = 0, fieldsLength = fields.length; fieldIdx < fieldsLength; fieldIdx++) {
            String field = fields[fieldIdx];
            for (int descriptionIdx = 0, descriptionsLength = columnNames.length; descriptionIdx < descriptionsLength; descriptionIdx++) {
                String description = columnNames[descriptionIdx];
                if (description.equals(field)) {
                    result[fieldIdx] = descriptionIdx;
                    break;
                }
            }
        }
        return result;
    }


    public static String[] calculateActualFields(final String[] fields, String[]... providedColumnNames) throws QueryUtilsException {
        if (fields == null) {
            return computeNewFields(providedColumnNames);
        } else {
            return computeMergedFields(fields, providedColumnNames);
        }
    }

    private static String[] computeMergedFields(final String[] fields, final String[]... descriptions) {
        String[] result = fields;
        for (String[] columnNames : descriptions) {
            result = StringUtil.retainAll(result, columnNames);
        }

        if (fields.length != result.length) {
            logger.warn("ConfigurationLabels provide " + fields.length + " fields, but only " + result.length + " fields are actually used.");
        } else {
            logger.debug("Using fields from configuration: " + Arrays.toString(result));
        }
        return result;
    }

    private static String[] computeNewFields(String[]... descriptions) throws QueryUtilsException {
        for (String[] desc : descriptions) {
            if (desc.length == 0) {
                throw new QueryUtilsException("No fields provided for comparison, and both queries are empty!");
            }
        }

        String[] result = descriptions[0];
        for (int descIdx = 1; descIdx < descriptions.length; descIdx++) {
            result = StringUtil.retainAll(result, descriptions[descIdx]);
        }
        logger.info("ConfigurationLabels provide no fields, so using query metadata: " + Arrays.toString(result));
        return result;
    }

    public static <T extends Tuple> void buildKey(T row, int[] keyColumnIdxs, Object[] keyValuesBuffer) {
        int keyIdx = 0;
        for (int keyColumnIdx : keyColumnIdxs) {
            keyValuesBuffer[keyIdx++] = row.getValue(keyColumnIdx);
        }
    }
}
