package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.PluginConfig;

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
    public void preComparison(ComparisonContext taijituData, PluginConfig comparisonConfig) throws TaijituException {

    }

    @Override
    public void postComparison(ComparisonContext taijituData, PluginConfig comparisonConfig) throws TaijituException {

    }
}