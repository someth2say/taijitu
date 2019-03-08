package org.someth2say.taijitu.cli.fileutil.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.fileutil.CommandException;
import org.someth2say.taijitu.cli.fileutil.FileCommand;
import org.someth2say.taijitu.cli.discarter.TimeBiDiscarter;

import java.io.*;
import java.util.function.BiConsumer;

/**
 * @author Jordi Sola
 */
public class CSVFileCommand extends FileCommand {

    private static final Logger logger = LoggerFactory.getLogger(CSVFileCommand.class);

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
            final BiConsumer<String, Object[]> discarter = new TimeBiDiscarter<>(1000, logger::info);
            int rowCount = 0;
            for (final Object[] row : values) {
                if (row == null) {
                    continue;
                }
                csvWriter.printRecords(row);
                discarter.accept("Wrote {} rows to CSV", new Object[]{++rowCount});
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
