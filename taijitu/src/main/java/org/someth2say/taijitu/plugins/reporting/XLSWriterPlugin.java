package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;

/**
 * @author Jordi Sola
 */
public class XLSWriterPlugin extends AbstractWriterPlugin {

    @Override
    public String getName() {
        return "xls";
    }

//    @Override
//    protected FileCommand getFileCommand(String fileNameSource, String sheetName, File outputFolder) throws CommandException {
//        return new XSLFileCommand(outputFolder, fileNameSource, sheetName);
//    }

    @Override
    public void preComparison(IPluginCfg comparisonConfig) throws TaijituException {

    }

    @Override
    public void postComparison(IPluginCfg comparisonConfig) throws TaijituException {

    }
}