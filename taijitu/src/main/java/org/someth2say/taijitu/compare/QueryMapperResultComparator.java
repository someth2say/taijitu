package org.someth2say.taijitu.compare;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.strategy.mapping.mapper.QueryMapperResult;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jordi Sola
 */
public final class QueryMapperResultComparator {
    private static final Logger logger = Logger.getLogger(QueryMapperResultComparator.class);

    private QueryMapperResultComparator() {
    }

    /**
     * Compares two maps of data, extracting
     * <ul>
     * <li>Elements missing on both source and target data, referring to each other.</li>
     * <li>Elements existing on both source and target data, but containing different values</li>
     * <li>Statistics from comparison results (i.e. proportion on how many elements on a column have different values)</li>
     * </ul>
     * <p>
     * Is important to note that <B>source and target data map values may be modified</B> during the process, removing from them elements that are <I>missing</I> on the other map.
     *
     * @param taijituData description for the comparison to be performed
     */
    public static void compare(final ComparisonRuntime taijituData, final QueryMapperResult<Integer, ComparableTuple> sourceMapperResult, final QueryMapperResult<Integer, ComparableTuple> targetMapperResult) {
        final ComparisonResult comparisonResult = taijituData.getResult();

        final Map<Integer, ComparableTuple> sourceMapValues = sourceMapperResult.getMapValues();
        final Map<Integer, ComparableTuple> targetMapValues = targetMapperResult.getMapValues();

        final String[] fields = taijituData.getFields();
        final String[] compareFields = taijituData.getCompareFields();
        final Map<Class<?>, Comparator<Object>> comparators = taijituData.getComparators();

        compareIntoResult(comparisonResult, sourceMapValues, targetMapValues, fields, compareFields, comparators);
    }

    public static void compareIntoResult(ComparisonResult comparisonResult, Map<Integer, ComparableTuple> sourceMapValues, Map<Integer, ComparableTuple> targetMapValues, String[] fields, String[] compareFields, Map<Class<?>, Comparator<Object>> comparators) {
        // Look for entries on one map that are missing on other map.
        // Modifies both sourceData and targetData: removes 'missing' elements in order to improve differences finding.
        findMissing(comparisonResult, sourceMapValues, targetMapValues);

        // Looks for remaining entries that are identified to be the same on both maps, but have different contents.
        findDifferent(comparisonResult, sourceMapValues, targetMapValues, comparators, fields, compareFields);
    }

    private static void findDifferent(final ComparisonResult comparisonResult, final Map<Integer, ComparableTuple> sourceData,
                                      final Map<Integer, ComparableTuple> targetData, Map<Class<?>, Comparator<Object>> comparators, String[] fields, String[] compareFields) {
        int[] compareFieldsIdxs = StringUtil.findIndexes(fields, compareFields);

        final List<Pair<ComparableTuple, ComparableTuple>> differences = new ArrayList<>();
        // Look for match elements, and then compare each field.
        for (final Entry<Integer, ComparableTuple> sourceEntry : sourceData.entrySet()) {
            final Integer sourceEntryKey = sourceEntry.getKey();
            if (targetData.containsKey(sourceEntryKey)) {
                // we have a match, but values the same?
                final ComparableTuple sourceValues = sourceData.get(sourceEntryKey);
                final ComparableTuple targetValues = targetData.get(sourceEntryKey);

                addIfDifferent(sourceValues, targetValues, comparators, compareFieldsIdxs, differences);
            }
        }

        comparisonResult.setDifferent(differences);
        logger.info("Entries on both tables but with different content: " + String.format("%,d", comparisonResult.getDifferent().size()));
    }

    private static boolean addIfDifferent(final ComparableTuple sourceObj, final ComparableTuple targetObj, Map<Class<?>, Comparator<Object>> comparators, int[] sourceCompareFieldsIdxs, List<Pair<ComparableTuple, ComparableTuple>> differences) {
        if (!sourceObj.equalsFields(targetObj, comparators, sourceCompareFieldsIdxs)) {
            differences.add(new ImmutablePair<>(sourceObj, targetObj));
            return true;
        }
        return false;
    }

    private static void findMissing(final ComparisonResult comparisonResult, final Map<Integer, ComparableTuple> sourceData,
                                    final Map<Integer, ComparableTuple> targetData) {

        final List<ComparableTuple> sourceOnly = findMissing(sourceData, targetData);
        comparisonResult.setSourceOnly(sourceOnly);
        logger.info("Entries on source only (missing in target): " + String.format("%,d", sourceOnly.size()));

        final List<ComparableTuple> targetOnly = findMissing(targetData, sourceData);
        comparisonResult.setTargetOnly(targetOnly);
        logger.info("Entries on target only (missing in source): " + String.format("%,d", targetOnly.size()));
    }

    private static List<ComparableTuple> findMissing(final Map<Integer, ComparableTuple> objectsMap, final Map<Integer, ComparableTuple> otherMap) {
        final List<ComparableTuple> missing = new ArrayList<>();
        // Looks for missing keys on second map
        final List<Integer> keysToBeRemoved = new ArrayList<>();
        for (final Entry<Integer, ComparableTuple> entry : objectsMap.entrySet()) {
            final Integer key = entry.getKey();
            if (!otherMap.containsKey(key)) {
                missing.add(objectsMap.get(key));
                keysToBeRemoved.add(key);
            }
        }
        // may remove from map, because no comparison will be performed
        for (final Integer key : keysToBeRemoved) {
            objectsMap.remove(key);
        }

        return missing;
    }


}
