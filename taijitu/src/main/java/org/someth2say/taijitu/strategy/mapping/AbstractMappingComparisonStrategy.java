package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.querywalker.MemStoreResults;
import org.someth2say.taijitu.query.querywalker.QueryWalker;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapper;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapperResult;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.compare.ComparableObjectArray;

import java.util.Collection;

/**
 * Created by Jordi Sola on 23/02/2017.
 */
public abstract class AbstractMappingComparisonStrategy implements ComparisonStrategy {
    private static final Logger logger = Logger.getLogger(AbstractMappingComparisonStrategy.class);


    protected void listValuesInto(Query sourceQuery, String queryName, ComparableObjectArray.Factory columnValueListFactory, MemStoreResults<ComparableObjectArray> result) throws TaijituException {
        try {
            logger.debug("Start getting values for " + queryName);
            QueryWalker.getMemStoreValuesInto(sourceQuery, columnValueListFactory, result);
            logger.debug("Completed getting values for " + queryName + ". Got " + String.format("%,d", result.getValues().size()) + " rows.");
        } catch (QueryUtilsException e) {
            throw new TaijituException("Exception when reading values from " + queryName, e);
        }
    }

    protected MemStoreResults<ComparableObjectArray> listValues(Query query) throws TaijituException {
        final String queryName = query.getQueryName();
        try {
            logger.debug("Start getting values for " + queryName);
            final MemStoreResults<ComparableObjectArray> result = QueryWalker.getMemStoreValues(query, ComparableObjectArray.Factory.INSTANCE);
            logger.debug("Completed getting values for " + queryName + ". Got " + String.format("%,d", result.getValues().size()) + " rows.");
            return result;
        } catch (QueryUtilsException e) {
            throw new TaijituException("Exception when reading values from " + queryName, e);
        }
    }

    protected QueryMapperResult<Integer, ComparableObjectArray> mapQueryResults(int[] keyFields, String queryName, Collection<ComparableObjectArray> values) {
        QueryMapperResult<Integer, ComparableObjectArray> result;
        logger.debug("Mapping values for " + queryName);
        result = QueryMapper.mapValues(keyFields, values);
        logger.debug("Completed mapping " + queryName + " for " + String.format("%,d", values.size()) + " rows.");
        return result;
    }

    protected void mapQueryResultsInto(int[] keyFields, String queryName, Collection<ComparableObjectArray> values, QueryMapperResult<Integer, ComparableObjectArray> results) {
        logger.debug("Mapping values for " + queryName);
        QueryMapper.mapValuesInto(keyFields, values, results);
        logger.debug("Completed mapping " + queryName + " for " + String.format("%,d", values.size()) + " rows.");
    }

}
