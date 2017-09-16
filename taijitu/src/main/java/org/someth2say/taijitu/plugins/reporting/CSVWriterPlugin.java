package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.fileutil.CommandException;
import org.someth2say.taijitu.fileutil.FileCommand;
import org.someth2say.taijitu.fileutil.csv.CSVFileCommand;

import java.io.File;

/**
 * @author Jordi Sola
 */
public class CSVWriterPlugin extends AbstractWriterPlugin {

    @Override
    protected FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException {
        return new CSVFileCommand(outputFolder, fileNameSource);
    }

    @Override
    public String getName() {
        return "csv";
    }

}