package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.fileutil.CommandException;
import org.someth2say.taijitu.fileutil.FileCommand;
import org.someth2say.taijitu.fileutil.xsl.XSLFileCommand;

import java.io.File;

/**
 * @author Jordi Sola
 */
public class XLSWriterPlugin extends AbstractWriterPlugin {

    @Override
    public String getName() {
        return "xls";
    }

    @Override
    protected FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException {
        return new XSLFileCommand(outputFolder, fileNameSource, sheetName);
    }

}