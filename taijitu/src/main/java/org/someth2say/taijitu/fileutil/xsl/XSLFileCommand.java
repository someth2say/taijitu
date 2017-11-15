package org.someth2say.taijitu.fileutil.xsl;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.someth2say.taijitu.fileutil.CommandException;
import org.someth2say.taijitu.fileutil.FileCommand;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jordi Sola
 */
public class XSLFileCommand extends FileCommand {

    private static final Logger logger = LoggerFactory.getLogger(XSLFileCommand.class);
    private final String sheetName;
    private final Integer targetRow;
    private final Integer targetColumn;

    public XSLFileCommand(final File folderName, final String fileName, final String _sheetName) throws CommandException {
        this(folderName, fileName, _sheetName, null, null);
    }

    public XSLFileCommand(final File folderName, final String fileName, final String _sheetName, final Integer row, final Integer column) throws CommandException {
        super(folderName, fileName);
        this.sheetName = _sheetName;
        this.targetRow = row;
        this.targetColumn = column;
    }

    @Override
    public void process(final OutputStream os, final Object payload) throws CommandException {
        if (!(payload instanceof String[][])) {
            throw new CommandException("Can't accept payload");
        }

        // Create or append book
        Workbook wb = createWorkbook();

        // Fill workbook
        final String[][] values = (String[][]) payload;
        Sheet sheet = fillWorkbook(wb, values);

        // Auto adjust sheet
        autoAdjustSheet(values, sheet);

        // write workbook
        try {
            wb.write(os);
        } catch (final IOException e) {
            throw new CommandException("Can not write to book to stream ", e);
        }
    }

    private void autoAdjustSheet(String[][] values, Sheet sheet) {
        if (values.length > 0) {
            for (int column = 0; column < values[0].length; column++) {
                sheet.autoSizeColumn(column);
            }
        }
    }

    private Sheet fillWorkbook(final Workbook wb, final String[][] values) {
        final Sheet sheet = wb.getSheet(this.sheetName);
        writeToWorkBook(sheet, values);
        return sheet;
    }

    private void writeToWorkBook(final Sheet sheet, String[][] values) {
        //TODO: Fix logging
//        Discarter discarter = TimeBasedLog4jDiscarter.newInstance(1000, logger, Level.INFO);

        final int startColumn = this.targetColumn == null ? 0 : this.targetColumn;
        int currentRow = getFirstEmptyRow(sheet, startColumn);
        for (final String[] sourceRow : values) {
            writeRow(sheet, startColumn, currentRow, sourceRow);
            currentRow++;
//            discarter.execute("Written ", currentRow, " rows to excel file");
        }
    }


    private void writeRow(Sheet sheet, int startColumn, int currentRow, String[] sourceRow) {
        int currentColumn = startColumn;
        final Row row = sheet.createRow(currentRow);
        for (final String value : sourceRow) {
            writeCell(currentColumn, row, value);
            currentColumn++;
        }
    }

    private void writeCell(int currentColumn, Row row, String value) {
        final Cell cell = row.createCell(currentColumn);
        if (value == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        try {
            // Number
            final double doubleValue = Double.parseDouble(value);
            cell.setCellValue(doubleValue);
            cell.setCellType(CellType.NUMERIC);

        } catch (final NumberFormatException e) {
            // Text
            cell.setCellValue(value);
            cell.setCellType(CellType.STRING);
        }
    }

    private int getFirstEmptyRow(Sheet sheet, int startColumn) {
        int currentRow;
        int startRow = 0;
        if (this.targetRow == null) {
            // look for first empty cell
            boolean emptyCellFound = false;
            for (final Row row : sheet) {
                final Cell startCell = row.getCell(startColumn);
                if (startCell != null && isCellEmpty(startCell)) {
                    emptyCellFound = true;
                    break;
                }
                startRow++;
            }
            if (!emptyCellFound) {
                sheet.createRow(startRow);
            }
        } else {
            startRow = this.targetRow;
        }
        currentRow = startRow;
        return currentRow;
    }

    private boolean isCellEmpty(final Cell cell) {
        //TODO: Upgrade
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                return true;
            case STRING:
                return "".equals(cell.getStringCellValue());
            case BOOLEAN:
            case ERROR:
            case FORMULA:
            case NUMERIC:
            default:
                return false;
        }

    }

    private Workbook createWorkbook() throws CommandException {
        Workbook wb;
        final File file = getFile();
        if (file.exists()) {
            logger.info("File {} already exist, and will be overwritten.",file.getName() );
        }
        if (file.getName().endsWith(".xls")) {
            wb = new HSSFWorkbook();
        } else {
            wb = new XSSFWorkbook();
        }
        wb.createSheet(sheetName);

        return wb;
    }

    @Override
    protected String getFileExtension() {
        return "xls";
    }
}
