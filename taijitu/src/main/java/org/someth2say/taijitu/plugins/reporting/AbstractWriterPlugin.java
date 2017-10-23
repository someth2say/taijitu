package org.someth2say.taijitu.plugins.reporting;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Pair;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public abstract class AbstractWriterPlugin implements TaijituPlugin {
    private static final String SOURCE_LABEL = "SOURCE";
    private static final String TARGET_LABEL = "TARGET";
    private static final Logger logger = Logger.getLogger(AbstractWriterPlugin.class);
    private File outputFolder;
    private WritterPluginConfig config;

    private String[][] printDifferent(final ComparisonResult comparisonResult, final ComparisonRuntime taijituData, final WritterPluginConfig config) {
//        this.config = config;
//        final Collection<Pair<ComparableTuple, ComparableTuple>> different = comparisonResult.getDifferent();
//        final String[][] result = new String[different.size() * 2 + 1][];
//
//        final List<String> fields = taijituData.getCanonicalColumns();
//        final List<String> keyFields = taijituData.getCanonicalKeys();
//
//        int rowIdx = 0;
//        if (!different.isEmpty()) {
//            final int fieldCount = fields.size();
//            // Headers
//            rowIdx = createRow(result, fieldCount, rowIdx, null);
//            System.arraycopy(fields, 0, result[0], 1, fieldCount);
//            highLightKeyFields(result[rowIdx], keyFields);
//
//            boolean[] keyFieldsMap = StringUtil.getBitMap(fields, keyFields);
//            boolean[] compareFieldsMap = StringUtil.getBitMap(fields, taijituData.getCanonicalColumns());
//            int[] sourceFieldToColumnsMap = ColumnDescriptionUtils.getFieldPositions(fields, comparisonResult.getSourceColumnDescriptions());
//            int[] targetFieldToColumnsMap = ColumnDescriptionUtils.getFieldPositions(fields, comparisonResult.getTargetColumnDescriptions());
//            // Contents
//            for (Pair<ComparableTuple, ComparableTuple> difference : different) {
//
//                final int sourceRowIdx = createRow(result, fieldCount, ++rowIdx, SOURCE_LABEL);
//                final int targetRowIdx = createRow(result, fieldCount, ++rowIdx, TARGET_LABEL);
//
//                copyDifferencesAndKeys(difference, result[sourceRowIdx], result[targetRowIdx], sourceFieldToColumnsMap, targetFieldToColumnsMap, keyFieldsMap, compareFieldsMap);
//            }
//        }
        return null;
    }

    private void highLightKeyFields(List<String> header, List<String> keyFields) {
        for (int columnIdx = 0; columnIdx < header.size(); columnIdx++) {
            if (keyFields.contains(header.get(columnIdx))) {
                header.set(columnIdx, header.get(columnIdx) + "*");
            }
        }
    }

    private int createRow(String[][] result, int fieldCount, int rowIdx, String label) {
        result[rowIdx] = new String[fieldCount + 1];
        result[rowIdx][0] = label;
        return rowIdx;
    }

    private void copyDifferencesAndKeys(Pair<ComparableTuple, ComparableTuple> difference, String[] sourceRow, String[] targetRow, int[] sourceFieldToColumnsMap, int[] targetFieldToColumnsMap, boolean[] keyFieldsMap, boolean[] compareFieldsMap) {
        final ComparableTuple sourceObjs = difference.getLeft();
        final ComparableTuple targetObjs = difference.getRight();

        for (int fieldIdx = 0; fieldIdx < sourceObjs.size(); fieldIdx++) {
            if (isKeyField(keyFieldsMap, fieldIdx) || (isCompareColumn(compareFieldsMap, fieldIdx) && isDifferent(sourceObjs, targetObjs, fieldIdx, sourceFieldToColumnsMap))) {
                final Object sourceValue = sourceObjs.getValue(sourceFieldToColumnsMap[fieldIdx]);
                final Object targetValue = targetObjs.getValue(targetFieldToColumnsMap[fieldIdx]);
                sourceRow[fieldIdx + 1] = sourceValue.toString();
                targetRow[fieldIdx + 1] = targetValue.toString();
            }
        }
    }

    private List<String>[] printMissing(final ComparisonRuntime comparison, Collection<ComparableTuple> missings) {
        final List<String>[] result = new List[missings.size() + 1];
        if (!missings.isEmpty()) {
            int idx = 0;
            // Headers
            result[idx] = comparison.getCanonicalColumns().stream().map(FieldDescription::getName).collect(Collectors.toList());

            final List<String> keyFields = comparison.getCanonicalKeys();
            highLightKeyFields(result[0], keyFields);

            // Contents
            for (ComparableTuple missing : missings) {
                result[++idx] = missing.toStringList();
            }
        }
        return result;
    }

    private boolean isCompareColumn(boolean[] compareFieldsMap, int fieldIdx) {
        return compareFieldsMap[fieldIdx];
    }

    private boolean isDifferent(ComparableTuple sourceRow, ComparableTuple targetRow, int fieldIdx, int[] fieldToColumnsMap) {
        //TODO: Fix dependency to comparators.
        return true;
        //return !sourceRow.isColumnEquals(targetRow, fieldToColumnsMap[fieldIdx], comparators);
    }

    private boolean isKeyField(boolean[] keyFieldsMap, int fieldIdx) {
        return keyFieldsMap[fieldIdx];
    }

    private boolean isReportable(final ComparisonResult comparisonResult) {
        return !comparisonResult.getDifferent().isEmpty() || !comparisonResult.getDisjoint().isEmpty();
    }
//
//    private File createOutputFolder() throws TaijituException {
//        final File result = config.getOutputFolderFile();
//        if (!result.exists()) {
//            final boolean dirCreated = result.mkdirs();
//            if (!dirCreated) {
//                throw new TaijituException("Error while trying to create output folder: " + result.getAbsolutePath());
//            }
//        }
//        return result;
//    }
//
//    private void writeResults(final ComparisonResult result, final ComparisonRuntime comparison, final String targetOnlyFile, final String sourceOnlyFile, final String diffsFile, final String testName, final File outputFolder) throws TaijituException, CommandException {
//        if (!result.getTargetOnly().isEmpty()) {
//            logger.debug("Writing entries only in target for " + testName);
//            final String[][] reportableStrings = printMissing(comparison, result.getTargetOnly());
//            getFileCommand(targetOnlyFile, comparison.getTestName(), outputFolder).process(reportableStrings);
//        }
//        if (!result.getSourceOnly().isEmpty()) {
//            logger.debug("Writing entries only in source for " + testName);
//            final String[][] reportableStrings = printMissing(comparison, result.getSourceOnly());
//            getFileCommand(sourceOnlyFile, comparison.getTestName(), outputFolder).process(reportableStrings);
//        }
//
//        if (!result.getDifferent().isEmpty()) {
//            logger.debug("Writing different entries for " + testName);
//            final String[][] reportableStrings = printDifferent(result, comparison);
//            getFileCommand(diffsFile, comparison.getTestName(), outputFolder).process(reportableStrings);
//        }
//    }
//
//    protected abstract FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException;

//    @Override
//    public void preComparison(ComparisonRuntime taijituData) throws TaijituException {
//        outputFolder = createOutputFolder();
//    }
//
//    @Override
//    public void postComparison(ComparisonRuntime taijituData) throws TaijituException {
//        final ComparisonResult result = taijituData.getResult();
//        final String testName = taijituData.getTestName();
//
//        if (isReportable(result)) {
//
//            final String targetOnlyFile = testName + ".target.only";
//            final String sourceOnlyFile = testName + ".source.only";
//            final String diffsFile = testName + ".difference";
//            try {
//                writeResults(result, taijituData, targetOnlyFile, sourceOnlyFile, diffsFile, testName, outputFolder);
//            } catch (CommandException e) {
//                throw new TaijituException("Unable to write results to " + getName(), e);
//            }
//
//        }
//
//    }

    @Override
    public void start(final PluginConfig config) throws TaijituException {
        // Do nothing
    }

    @Override
    public void end(final PluginConfig config) throws TaijituException {
        // Do nothing
    }

}
