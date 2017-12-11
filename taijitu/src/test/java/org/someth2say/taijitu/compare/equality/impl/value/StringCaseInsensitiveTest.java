package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableHashable;

import static org.junit.Assert.*;

public class StringCaseInsensitiveTest {

    StringCaseInsensitive instance = new StringCaseInsensitive();
    ComparableHashable<String> aaa = instance.wrap("aaa");

    @Test
    public void hashCodeTest() {
        assertEquals(instance.hashCode("aaa"), instance.hashCode("aAA"));
        assertNotEquals(instance.hashCode("aaa"), instance.hashCode("aa"));

        assertEquals(aaa.hashCode(), instance.hashCode("aaa"));

    }

    @Test
    public void equalsTest() {
        assertTrue(instance.equals("aaa", "aAA"));
        assertFalse(instance.equals("aaa", "aa"));

        //TODO: This is awkward, objects should only be compared to other objects on the same type hierarchy...
        assertTrue(aaa.equalsTo("aaa"));

        assertTrue(aaa.equals(instance.wrap("aAA")));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare("aaa", "aAA") == 0);
        assertFalse(instance.compare("aaa", "aa") < 0);
        assertFalse(instance.compare("aa", "aaA") > 0);

        assertTrue(aaa.compareTo("aAA") == 0);
        assertFalse(aaa.compareTo("aa") < 0);
        assertFalse(instance.wrap("aa").compareTo("aa") > 0);
    }
}