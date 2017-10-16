package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapperResult;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.QueryMapperResultComparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class InteractiveMappingStrategy extends AbstractMappingComparisonStrategy {

    public static final String NAME = "interactive";
    private static Logger logger = Logger.getLogger(InteractiveMappingStrategy.class);

    @Override
    public void runComparison(final ComparisonRuntime taijituData) throws TaijituException {

        logger.debug("Starting queries for " + taijituData.getTestName());
        final ComparisonResult result = taijituData.getResult();

        final Query sourceQuery = taijituData.getSource();
        final String sourceName = sourceQuery.getQueryName();
        waitForKeyPress(sourceName, "listing");
        MemStoreResults<ComparableTuple> sourceQLR = listValues(sourceQuery);

        final Query targetQuery = taijituData.getTarget();
        final String targetName = targetQuery.getQueryName();
        waitForKeyPress(targetName, "listing");
        MemStoreResults<ComparableTuple> targetQLR = listValues(targetQuery);

        //3.- Update fields with column descriptions
        // Comparison Fields may have been set after getting values!
        final String[] sourceDescriptions = sourceQLR.getDescriptions();
        final String[] targetDescriptions = targetQLR.getDescriptions();

        result.setSourceColumnDescriptions(sourceDescriptions);
        result.setTargetColumnDescriptions(targetDescriptions);
        taijituData.calculateActualFields();

        //4.- Perform mappings
        logger.debug("Starting mapping results for " + taijituData.getTestName());

        final String[] keyFields = taijituData.getKeyFields();
        final int[] sourceKeyFieldsPositions = ColumnDescriptionUtils.getFieldPositions(keyFields, sourceDescriptions);
        final int[] targetKeyFieldsPositions = ColumnDescriptionUtils.getFieldPositions(keyFields, targetDescriptions);

        waitForKeyPress(sourceName, "mapping");
        final QueryMapperResult<Integer, ComparableTuple> sourceMapperResult = mapQueryResults(sourceKeyFieldsPositions, sourceName, sourceQLR.getValues());
        //result.setSourceMap(sourceMapperResult);

        waitForKeyPress(targetName, "mapping");
        final QueryMapperResult<Integer, ComparableTuple> targetMapperResult = mapQueryResults(targetKeyFieldsPositions, targetName, targetQLR.getValues());
        //result.setTargetMap(targetMapperResult);

        //5.- Compare maps
        logger.debug("Starting comparison for " + taijituData.getTestName());
        String[] fields = taijituData.getFields();
        String[] compareFields = taijituData.getCompareFields();
        Map<Class<?>, Comparator<Object>> comparators = taijituData.getComparators();
        QueryMapperResultComparator.compareIntoResult(result, sourceMapperResult.getMapValues(), targetMapperResult.getMapValues(), fields, compareFields, comparators);
        logger.debug("Comparison complete for " + taijituData.getTestName());
    }

    private void waitForKeyPress(String sourceName, String action) throws TaijituException {
        System.out.println("Press enter to start " + action + " values for " + sourceName);
        Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
