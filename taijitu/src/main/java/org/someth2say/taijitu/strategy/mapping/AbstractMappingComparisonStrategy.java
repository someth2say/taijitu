package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapper;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapperResult;
import org.someth2say.taijitu.TaijituException;

import java.util.Collection;

/**
 * Created by Jordi Sola on 23/02/2017.
 */
public abstract class AbstractMappingComparisonStrategy implements ComparisonStrategy {
    private static final Logger logger = Logger.getLogger(AbstractMappingComparisonStrategy.class);


    protected void listValuesInto(Query sourceQuery, String queryName, ComparableTuple.Factory columnValueListFactory, MemStoreResults<ComparableTuple> result) throws TaijituException {
        try {
            logger.debug("Start getting values for " + queryName);
            QueryWalker.getMemStoreValuesInto(sourceQuery, columnValueListFactory, result);
            logger.debug("Completed getting values for " + queryName + ". Got " + String.format("%,d", result.getValues().size()) + " rows.");
        } catch (QueryUtilsException e) {
            throw new TaijituException("Exception when reading values from " + queryName, e);
        }
    }

    protected MemStoreResults<ComparableTuple> listValues(Query query) throws TaijituException {
        final String queryName = query.getQueryName();
        try {
            logger.debug("Start getting values for " + queryName);
            final MemStoreResults<ComparableTuple> result = QueryWalker.getMemStoreValues(query, ComparableTuple.Factory.INSTANCE);
            logger.debug("Completed getting values for " + queryName + ". Got " + String.format("%,d", result.getValues().size()) + " rows.");
            return result;
        } catch (QueryUtilsException e) {
            throw new TaijituException("Exception when reading values from " + queryName, e);
        }
    }

    protected QueryMapperResult<Integer, ComparableTuple> mapQueryResults(int[] keyFields, String queryName, Collection<ComparableTuple> values) {
        QueryMapperResult<Integer, ComparableTuple> result;
        logger.debug("Mapping values for " + queryName);
        result = QueryMapper.mapValues(keyFields, values);
        logger.debug("Completed mapping " + queryName + " for " + String.format("%,d", values.size()) + " rows.");
        return result;
    }

    protected void mapQueryResultsInto(int[] keyFields, String queryName, Collection<ComparableTuple> values, QueryMapperResult<Integer, ComparableTuple> results) {
        logger.debug("Mapping values for " + queryName);
        QueryMapper.mapValuesInto(keyFields, values, results);
        logger.debug("Completed mapping " + queryName + " for " + String.format("%,d", values.size()) + " rows.");
    }

}
