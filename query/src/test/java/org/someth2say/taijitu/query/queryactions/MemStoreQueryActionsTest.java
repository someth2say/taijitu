package org.someth2say.taijitu.query.queryactions;

import org.junit.Test;
import org.someth2say.taijitu.query.objects.ObjectArray;
import org.someth2say.taijitu.query.objects.DefaultObjectArray;

import static org.junit.Assert.assertTrue;

/**
 * Created by Jordi Sola on 10/03/2017.
 */
public class MemStoreQueryActionsTest {
    @Test
    public void start() throws Exception {
        MemStoreQueryActions<ObjectArray> msqa = new MemStoreQueryActions<>();
        String[] cd = new String[]{};
        msqa.start(cd);
        assertTrue("Column descriptions should be retained", cd == msqa.getColumnDescriptions());
    }

    @Test
    public void step() throws Exception {
        MemStoreQueryActions<ObjectArray> msqa = new MemStoreQueryActions<>();
        ObjectArray obj = DefaultObjectArray.Factory.INSTANCE.getInstance(new Object[]{});
        msqa.step(obj);
        assertTrue("Stepped objects should be retained", msqa.getValues().contains(obj));
    }

    @Test
    public void end() throws Exception {
        // Does nothing. but should not fail
        new MemStoreQueryActions<>().end();
    }
}