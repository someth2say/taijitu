package org.someth2say.taijitu.ui.fileutil.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jordi Sola on 10/03/2017.
 */
class CSVFileCommandTest {
    private final String filename = "test";
    private final String[][] vals = new String[][]{new String[]{null, "Some", "Value"}};
    private final File folder = new File(".");

    //@Test
    public void process() throws Exception {
        final CSVFileCommand csvFileCommand = new CSVFileCommand(folder, filename);
        csvFileCommand.process(vals);
        final File file = new File(folder, filename + "." + csvFileCommand.getFileExtension());
        assertTrue("CSV file should be created", file.exists());
        csvFileCommand.rollback();
    }

    //@Test
    public void processWriter() throws Exception {
        final CSVFileCommand csvFileCommand = new CSVFileCommand(folder, filename);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        csvFileCommand.process(os, vals);
        assertTrue("Contents should not be empty", !os.toString().isEmpty());
        csvFileCommand.rollback();
    }

    //@Test
    public void testAppend() throws Exception {
        final CSVFileCommand csvFileCommand = new CSVFileCommand(folder, filename);
        csvFileCommand.setAppend(false);
        assertTrue("Append should be retained", !csvFileCommand.isAppend());
        csvFileCommand.setAppend(true);
        assertTrue("Append should be retained", csvFileCommand.isAppend());
    }

    //@Test
    public void getFileExtension() throws Exception {
        assertEquals("Extension should be fixed", new CSVFileCommand(folder, filename).getFileExtension(), "csv");
    }

}