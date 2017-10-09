package org.someth2say.taijitu.query.queryactions;

import org.junit.Test;
import org.someth2say.taijitu.query.tuple.DefaultTuple;
import org.someth2say.taijitu.query.tuple.Tuple;

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
        OutputStreamQueryActions<Tuple> msqa = new OutputStreamQueryActions<>(os);
        String[] cd = new String[]{};
        msqa.start(cd);
        assertTrue("Column descriptions should be retained", cd == msqa.getColumnDescriptions());
    }

    @Test
    public void step() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamQueryActions<Tuple> msqa = new OutputStreamQueryActions<>(os);

        String[] cd = new String[]{"ColumnName"};
        msqa.start(cd);

        Tuple obj = DefaultTuple.Factory.INSTANCE.getInstance(new Object[]{});
        msqa.step(obj);
        final String string = os.toString();
        assertTrue("Stepped tuple should be retained", !string.isEmpty());
        assertTrue("Headers should be dumped", string.contains("ColumnName"));
    }

    @Test
    public void end() throws Exception {
        new OutputStreamQueryActions<>(new ByteArrayOutputStream()).end();
    }
}