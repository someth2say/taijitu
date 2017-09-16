package org.someth2say.taijitu.query.queryactions;

import org.junit.Test;
import org.someth2say.taijitu.query.objects.DefaultObjectArray;
import org.someth2say.taijitu.query.objects.ObjectArray;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class OutputStreamQueryActionsTest {

    @Test
    public void start() throws Exception {
        OutputStream os = new ByteArrayOutputStream();
        OutputStreamQueryActions<ObjectArray> msqa = new OutputStreamQueryActions<>(os);
        String[] cd = new String[]{};
        msqa.start(cd);
        assertTrue("Column descriptions should be retained", cd == msqa.getColumnDescriptions());
    }

    @Test
    public void step() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamQueryActions<ObjectArray> msqa = new OutputStreamQueryActions<>(os);

        String[] cd = new String[]{"ColumnName"};
        msqa.start(cd);

        ObjectArray obj = DefaultObjectArray.Factory.INSTANCE.getInstance(new Object[]{});
        msqa.step(obj);
        final String string = os.toString();
        assertTrue("Stepped objects should be retained", !string.isEmpty());
        assertTrue("Headers should be dumped", string.contains("ColumnName"));
    }

    @Test
    public void end() throws Exception {
        new OutputStreamQueryActions<>(new ByteArrayOutputStream()).end();
    }
}