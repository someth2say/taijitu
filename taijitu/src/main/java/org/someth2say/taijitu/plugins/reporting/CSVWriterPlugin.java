package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.TaijituException;

/**
 * @author Jordi Sola
 */
public class CSVWriterPlugin extends AbstractWriterPlugin {

//    @Override
//    protected FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException {
//        return new CSVFileCommand(outputFolder, fileNameSource);
//    }

    @Override
    public String getName() {
        return "csv";
    }

    @Override
    public void preComparison(ComparisonContext taijituData, PluginConfigIface comparisonConfig) throws TaijituException {

    }

    @Override
    public void postComparison(ComparisonContext taijituData, PluginConfigIface comparisonConfig) throws TaijituException {

    }
}