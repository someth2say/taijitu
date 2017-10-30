package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.TaijituException;

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
    public void preComparison(ComparisonContext taijituData, PluginConfigIface comparisonConfig) throws TaijituException {

    }

    @Override
    public void postComparison(ComparisonContext taijituData, PluginConfigIface comparisonConfig) throws TaijituException {

    }
}