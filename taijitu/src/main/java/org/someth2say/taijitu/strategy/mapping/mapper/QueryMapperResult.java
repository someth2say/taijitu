package org.someth2say.taijitu.strategy.mapping.mapper;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.someth2say.taijitu.query.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jordi Sola
 */
@Deprecated
public class QueryMapperResult<KEY, VALUE extends Tuple> {
    final private MultiValuedMap<KEY, VALUE> duplicatedKeyValues = new ArrayListValuedHashMap<>();

    private Map<KEY, VALUE> mapValues = new HashMap<>();

    public Map<KEY, VALUE> getMapValues() {
        return this.mapValues;
    }

    public void setMapValues(HashMap<KEY, VALUE> _mapValues) {
        this.mapValues = _mapValues;
    }

    public MultiValuedMap<KEY, VALUE> getDuplicatedKeyValues() {
        return this.duplicatedKeyValues;
    }

}
