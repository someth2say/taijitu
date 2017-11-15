package org.someth2say.taijitu.fileutil.xsl;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jordi Sola on 10/03/2017.
 */
class XSLFileCommandTest {

    private final String filename = "test";
    private final String[][] vals = new String[][]{new String[]{null, "Some", "Value"}};
    private final File folder = new File(".");
    private final String sheetname = "sheet";

    //@Test
    public void process() throws Exception {
        final XSLFileCommand fileCommand = new XSLFileCommand(folder, filename, sheetname);
        fileCommand.process(vals);
        final File file = new File(folder, filename + "." + fileCommand.getFileExtension());
        assertTrue("CSV file should be created", file.exists());
        file.delete();
    }

    //@Test
    public void processWriter() throws Exception {
        final XSLFileCommand fileCommand = new XSLFileCommand(folder, filename, sheetname);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fileCommand.process(os, vals);
        assertTrue("Contents should not be empty", !os.toString().isEmpty());
    }

    //@Test
    public void getFileExtension() throws Exception {
        assertEquals("Extension should be fixed", new XSLFileCommand(folder, filename, sheetname).getFileExtension(), "xls");
    }

}