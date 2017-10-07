package org.someth2say.taijitu.plugins.reporting;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.fileutil.CommandException;
import org.someth2say.taijitu.fileutil.FileCommand;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.compare.ComparableObjectArray;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonPluginConfig;
import org.someth2say.taijitu.config.TaijituConfigImpl;
import org.someth2say.taijitu.util.Pair;

import java.io.File;
import java.util.*;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public abstract class AbstractWriterPlugin implements TaijituPlugin {
    private static final String SOURCE_LABEL = "SOURCE";
    private static final String TARGET_LABEL = "TARGET";
    private static final Logger logger = Logger.getLogger(AbstractWriterPlugin.class);
    private File outputFolder;

    private String[][] printDifferent(final ComparisonResult comparisonResult, final ComparisonRuntime taijituData) {
        final Collection<Pair<ComparableObjectArray, ComparableObjectArray>> different = comparisonResult.getDifferent();
        final String[][] result = new String[different.size() * 2 + 1][];

        final String[] fields = taijituData.getFields();
        final String[] keyFields = taijituData.getKeyFields();

        int rowIdx = 0;
        if (!different.isEmpty()) {
            final int fieldCount = fields.length;
            // Headers
            rowIdx = createRow(result, fieldCount, rowIdx, null);
            System.arraycopy(fields, 0, result[0], 1, fieldCount);
            highLightKeyFields(result[rowIdx], keyFields);

            boolean[] keyFieldsMap = StringUtil.getBitMap(fields, keyFields);
            boolean[] compareFieldsMap = StringUtil.getBitMap(fields, taijituData.getCompareFields());
            int[] sourceFieldToColumnsMap = ColumnDescriptionUtils.getFieldPositions(fields, taijituData.getResult().getSourceColumnDescriptions());
            int[] targetFieldToColumnsMap = ColumnDescriptionUtils.getFieldPositions(fields, taijituData.getResult().getTargetColumnDescriptions());
            Map<Class<?>, Comparator<Object>> comparators = taijituData.getComparators();
            // Contents
            for (Pair<ComparableObjectArray, ComparableObjectArray> difference : different) {

                final int sourceRowIdx = createRow(result, fieldCount, ++rowIdx, SOURCE_LABEL);
                final int targetRowIdx = createRow(result, fieldCount, ++rowIdx, TARGET_LABEL);

                copyDifferencesAndKeys(difference, result[sourceRowIdx], result[targetRowIdx], sourceFieldToColumnsMap, targetFieldToColumnsMap, keyFieldsMap, compareFieldsMap, comparators);
            }
        }
        return result;
    }

    private void highLightKeyFields(String[] header, String[] keyFields) {
        final List<String> keyFieldsList = Arrays.asList(keyFields);
        for (int columnIdx = 0; columnIdx < header.length; columnIdx++) {
            if (keyFieldsList.contains(header[columnIdx])) {
                header[columnIdx] = header[columnIdx] + "*";
            }
        }
    }

    private int createRow(String[][] result, int fieldCount, int rowIdx, String label) {
        result[rowIdx] = new String[fieldCount + 1];
        result[rowIdx][0] = label;
        return rowIdx;
    }

    private void copyDifferencesAndKeys(Pair<ComparableObjectArray, ComparableObjectArray> difference, String[] sourceRow, String[] targetRow, int[] sourceFieldToColumnsMap, int[] targetFieldToColumnsMap, boolean[] keyFieldsMap, boolean[] compareFieldsMap, Map<Class<?>, Comparator<Object>> comparators) {
        final ComparableObjectArray sourceObjs = difference.getLeft();
        final ComparableObjectArray targetObjs = difference.getRight();

        for (int fieldIdx = 0; fieldIdx < sourceObjs.size(); fieldIdx++) {
            if (isKeyField(keyFieldsMap, fieldIdx) || (isCompareColumn(compareFieldsMap, fieldIdx) && isDifferent(sourceObjs, targetObjs, fieldIdx, sourceFieldToColumnsMap, comparators))) {
                final Object sourceValue = sourceObjs.getValue(sourceFieldToColumnsMap[fieldIdx]);
                final Object targetValue = targetObjs.getValue(targetFieldToColumnsMap[fieldIdx]);
                sourceRow[fieldIdx + 1] = sourceValue.toString();
                targetRow[fieldIdx + 1] = targetValue.toString();
            }
        }
    }

    private String[][] printMissing(final ComparisonRuntime comparison, Collection<ComparableObjectArray> missings) {
        final String[][] result = new String[missings.size() + 1][];
        if (!missings.isEmpty()) {
            int idx = 0;
            // Headers
            result[idx] = comparison.getFields().clone();
            final String[] keyFields = comparison.getKeyFields();
            highLightKeyFields(result[0], keyFields);

            // Contents
            for (ComparableObjectArray missing : missings) {
                result[++idx] = missing.toStringArray();
            }
        }
        return result;
    }

    private boolean isCompareColumn(boolean[] compareFieldsMap, int fieldIdx) {
        return compareFieldsMap[fieldIdx];
    }

    private boolean isDifferent(ComparableObjectArray sourceRow, ComparableObjectArray targetRow, int fieldIdx, int[] fieldToColumnsMap, Map<Class<?>, Comparator<Object>> comparators) {
        return !sourceRow.isColumnEquals(targetRow, fieldToColumnsMap[fieldIdx], comparators);
    }

    private boolean isKeyField(boolean[] keyFieldsMap, int fieldIdx) {
        return keyFieldsMap[fieldIdx];
    }

    private boolean isReportable(final ComparisonResult comparisonResult) {
        return !comparisonResult.getTargetOnly().isEmpty() || !comparisonResult.getSourceOnly().isEmpty() || !comparisonResult.getDifferent().isEmpty();
    }

    private File createOutputFolder() throws TaijituException {
        final File result = TaijituConfigImpl.getOutputFolderFile();
        if (!result.exists()) {
            final boolean dirCreated = result.mkdirs();
            if (!dirCreated) {
                throw new TaijituException("Error while trying to create output folder: " + result.getAbsolutePath());
            }
        }
        return result;
    }

    private void writeResults(final ComparisonResult result, final ComparisonRuntime comparison, final String targetOnlyFile, final String sourceOnlyFile, final String diffsFile, final String testName, final File outputFolder) throws TaijituException, CommandException {
        if (!result.getTargetOnly().isEmpty()) {
            logger.debug("Writing entries only in target for " + testName);
            final String[][] reportableStrings = printMissing(comparison, result.getTargetOnly());
            getFileCommand(targetOnlyFile, comparison.getTestName(), outputFolder).process(reportableStrings);
        }
        if (!result.getSourceOnly().isEmpty()) {
            logger.debug("Writing entries only in source for " + testName);
            final String[][] reportableStrings = printMissing(comparison, result.getSourceOnly());
            getFileCommand(sourceOnlyFile, comparison.getTestName(), outputFolder).process(reportableStrings);
        }

        if (!result.getDifferent().isEmpty()) {
            logger.debug("Writing different entries for " + testName);
            final String[][] reportableStrings = printDifferent(result, comparison);
            getFileCommand(diffsFile, comparison.getTestName(), outputFolder).process(reportableStrings);
        }
    }

    protected abstract FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException;

    @Override
    public void preComparison(ComparisonRuntime taijituData) throws TaijituException {
        outputFolder = createOutputFolder();
    }

    @Override
    public void postComparison(ComparisonRuntime taijituData) throws TaijituException {
        final ComparisonResult result = taijituData.getResult();
        final String testName = taijituData.getTestName();

        if (isReportable(result)) {

            final String targetOnlyFile = testName + ".target.only";
            final String sourceOnlyFile = testName + ".source.only";
            final String diffsFile = testName + ".difference";
            try {
                writeResults(result, taijituData, targetOnlyFile, sourceOnlyFile, diffsFile, testName, outputFolder);
            } catch (CommandException e) {
                throw new TaijituException("Unable to write results to " + getName(), e);
            }

        }

    }

    @Override
    public void start(final ComparisonPluginConfig config) throws TaijituException {
        // Do nothing
    }

    @Override
    public void end(final ComparisonPluginConfig config) throws TaijituException {
        // Do nothing
    }

}
