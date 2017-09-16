package org.someth2say.taijitu.query.properties;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Jordi Sola on 13/03/2017.
 */
public class HPropertiesHelperTest {


    @Test
    public void getPropertiesByPrefix() throws Exception {
        HProperties hp = new HProperties();
        hp.put("prefix.a", "1");
        hp.put("prefix.b", "1");
        hp.put("prefix.c.1", "2");
        hp.put("prefix.c.2", "2");
        hp.put("prefix.d", "3");
        hp.put("noprefix.c", "x");

        final HProperties p1 = hp.getPropertiesByPrefix("prefix");
        assertEquals("All prefix properties should be retained", 5, p1.size());
        assertFalse("Non prefix matching properties should not be present", p1.containsKey("noprefix.c"));

        final HProperties p2 = hp.getPropertiesByPrefix("prefix", "c");
        assertEquals("Multi-level prefixes should work properly", 2, p2.size());
        assertFalse("Non maching multi-level properties should not be present", p2.containsKey("noprefix.c"));

    }


    @Test
    public void getPropertiesByPrefix1() throws Exception {
        HProperties hp = new HProperties();
        hp.put("prefix.aa", "1");
        hp.put("prefix.ab", "1");
        hp.put("prefix.ac.1", "2");
        hp.put("prefix.ac.2", "2");
        hp.put("prefix.d", "3");
        hp.put("noprefix.c", "x");

        final HProperties p1 = hp.getPropertiesByPrefix("prefix.a", false);
        assertEquals("All prefix properties should be retained", 4, p1.size());
        assertTrue("Prefixes should be retained when asked", p1.containsKey("prefix.ab"));
        assertFalse("Non prefix matching properties should not be present", p1.containsKey("prefix.d"));

        final HProperties p2 = hp.getPropertiesByPrefix("prefix.", true);
        assertEquals("All prefix properties should be retained", 5, p2.size());
        assertTrue("Prefixes should be removed when asked", p2.containsKey("ac.1"));
    }


    @Test
    public void getSubPropertiesByPrefix() throws Exception {
        HProperties hp = new HProperties();
        hp.put("prefix.a", "1");
        hp.put("prefix.b", "1");
        hp.put("prefix.c.1", "2");
        hp.put("prefix.c.2", "2");
        hp.put("prefix.d", "3");
        hp.put("noprefix.c", "x");

        final HProperties p1 = hp.getSubPropertiesByPrefix("prefix");
        assertEquals("All prefix properties should be retained", 5, p1.size());
        assertFalse("Non prefix matching properties should not be present", p1.containsKey("c"));

        final HProperties p2 = hp.getSubPropertiesByPrefix("prefix", "c");
        assertEquals("Multi-level prefixes should work properly", 2, p2.size());
        assertFalse("Non matching multi-level properties should not be present", p2.containsKey("c"));
    }

    @Test
    public void getPropertiesRoots() throws Exception {
        HProperties hp = new HProperties();
        hp.put("prefix.a", "1");
        hp.put("prefix.b", "1");
        hp.put("prefix.c.1", "2");
        hp.put("prefix.c.2", "2");
        hp.put("prefix.d", "3");
        hp.put("noprefix.c", "x");

        final Set<String> p1 = hp.getPropertiesRoots();
        assertEquals("All roots should be available", 2, p1.size());
        assertTrue(p1.contains("prefix"));
        assertTrue(p1.contains("noprefix"));
    }

    @Test
    public void getHierarchicalProperty() throws Exception {
        HProperties hp = new HProperties();
        hp.put("a", "0");
        hp.put("a.a.a", "1");
        hp.put("a.a.b", "2");
        hp.put("a.b.a", "3");
        hp.put("a.b.b", "4");
        hp.put("a.c.a", "5");
        hp.put("a.c.c", "6");
        hp.put("b.a.a", "7");
        hp.put("b.a.b", "8");
        hp.put("b.b.a", "9");
        hp.put("c.a", "10");
        hp.put("c.b", "11");

        assertEquals("1", hp.getHierarchycalProperty("a", "a", "a"));
        assertEquals("10", hp.getHierarchycalProperty("a", "c", "a"));
        assertEquals("1", hp.getHierarchycalProperty("a", null, "a.a"));
        assertEquals("0", hp.getHierarchycalProperty("a", null, "a"));
    }

    @Test
    public void load() throws Exception {
        final HProperties hp1 = HPropertiesHelper.load(new ByteArrayInputStream("".getBytes()));
        assertTrue("Nothing to load from empty string", hp1.isEmpty());

        final HProperties hp2 = HPropertiesHelper.load(new ByteArrayInputStream("a.a=1".getBytes()));
        assertEquals("Nothing to load from empty string", 1, hp2.size());
        assertEquals("Loaded values should be retained", "1", hp2.get("a.a"));

        //TODO Test Imports

    }


    @Test
    public void joinSections() throws Exception {
        HProperties hp = new HProperties("/");
        assertEquals("Joined sections should use separator", hp.joinSections("1", "2", "3"), "1/2/3");
        assertEquals("Joined partial sections should satisfy limits", hp.joinSections(1, 3, "0", "1", "2", "3", "4"), "1/2");
    }


    @Test
    public void getProperty() throws Exception {
        HProperties hp = new HProperties();
        hp.put("a.b.c", "0");
        assertEquals("Getting should satisfy sections", hp.getProperty("c", "a", "b"), "0");
        assertEquals("Getting should not be hierarchical", hp.getProperty("c", "a"), null);
    }

}