package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;

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
    public void preComparison(IPluginCfg comparisonConfig) throws TaijituException {

    }

    @Override
    public void postComparison(IPluginCfg comparisonConfig) throws TaijituException {

    }
}