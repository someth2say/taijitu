package org.someth2say.taijitu.strategy.mapping.mapper;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.query.tuple.Tuple;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Jordi Sola
 */
@Deprecated
public final class QueryMapper {
    private static final Logger logger = Logger.getLogger(QueryMapper.class);

    private QueryMapper() {
    }

    public static <T extends Tuple> QueryMapperResult<Integer, T> mapValues(int[] keyColumnIdxs, Collection<T> values) {
        QueryMapperResult<Integer, T> result = new QueryMapperResult<>();
        mapValuesInto(keyColumnIdxs, values, result);
        return result;
    }

    public static <T extends Tuple> void mapValuesInto(int[] keyColumnIdxs, Collection<T> values, QueryMapperResult<Integer, T> result) {
        result.setMapValues(new HashMap<>());
        Object[] keyValuesBuffer = new Object[keyColumnIdxs.length];
        for (T row : values) {
            mapRow(keyColumnIdxs, result, keyValuesBuffer, row);
        }

        if (!result.getDuplicatedKeyValues().isEmpty()) {
            logger.warn("Found " + result.getDuplicatedKeyValues().size() + " duplicated keys. This may cause false missing entries!");
        }
    }

    public static <K, T extends Tuple> void mapRow(int[] keyColumnIdxs, final QueryMapperResult<Integer, T> result, Object[] keyValuesBuffer, T row) {
        int key = buildIntKey(row, keyColumnIdxs, keyValuesBuffer);
        if (result.getMapValues().containsKey(key)) {
            result.getDuplicatedKeyValues().put(key, row);
        }
        result.getMapValues().put(key, row);
    }

    public static <T extends Tuple> int buildIntKey(T row, int[] keyColumnIdxs, Object[] keyValuesBuffer) {
        ColumnDescriptionUtils.buildKey(row, keyColumnIdxs, keyValuesBuffer);
        return java.util.Objects.hash(keyValuesBuffer);
    }

}
