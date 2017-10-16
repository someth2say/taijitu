package org.someth2say.taijitu.fileutil.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.fileutil.CommandException;
import org.someth2say.taijitu.fileutil.FileCommand;
import org.someth2say.taijitu.discarter.Discarter;
import org.someth2say.taijitu.discarter.TimeBasedLog4jDiscarter;

import java.io.*;

/**
 * @author Jordi Sola
 */
public class CSVFileCommand extends FileCommand {

    private static final Logger logger = Logger.getLogger(CSVFileCommand.class);

    public CSVFileCommand(final File folder, final String file) throws CommandException {
        super(folder, file);
    }

    @Override
    public void process(final OutputStream outputStream, final Object payload) throws CommandException {
        if (!(payload instanceof Object[][])) {
            throw new CommandException("Can't accept payload");
        }
        Object[][] values = (Object[][]) payload;
        try (final Writer osWriter = new OutputStreamWriter(outputStream); final CSVPrinter csvWriter = CSVFormat.DEFAULT.print(osWriter)) {
            final Discarter discarter = TimeBasedLog4jDiscarter.newInstance(1000, logger, Level.INFO);
            int rowCount = 0;
            for (final Object[] row : values) {
                if (row == null) {
                    continue;
                }
                csvWriter.printRecords(row);
                discarter.execute("Wrote ", Integer.toString(++rowCount), " rows to CSV");
            }
        } catch (IOException e) {
            throw new CommandException((new StringBuilder()).append("Unable to process CSV data ").toString(), e);
        }
    }

    @Override
    protected String getFileExtension() {
        return "csv";
    }

}
