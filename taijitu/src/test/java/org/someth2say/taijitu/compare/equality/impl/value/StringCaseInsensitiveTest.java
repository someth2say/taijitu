package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringCaseInsensitiveTest {

    StringCaseInsensitive instance = new StringCaseInsensitive();
    @Test
    public void hashCodeTest() {
        assertEquals(instance.hashCode("aaa"),instance.hashCode("aAA"));
        assertNotEquals(instance.hashCode("aaa"),instance.hashCode("aa"));
    }

    @Test
    public void equalsTest() {
        assertTrue(instance.equals("aaa","aAA"));
        assertFalse(instance.equals("aaa","aa"));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare("aaa","aAA")==0);
        assertFalse(instance.compare("aaa","aa")<0);
        assertFalse(instance.compare("aa","aaA")>0);
    }
}