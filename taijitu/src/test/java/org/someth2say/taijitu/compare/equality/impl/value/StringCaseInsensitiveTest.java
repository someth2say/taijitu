package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableHashableWrapper;

import static org.junit.Assert.*;

public class StringCaseInsensitiveTest {

    final StringCaseInsensitive instance = new StringCaseInsensitive();
    final IComparableHashableWrapper<String, ?> aaa = instance.wrap("aaa");
    final IComparableHashableWrapper<String, ?> aAA = instance.wrap("aAA");
    final IComparableHashableWrapper<String, ?> aa = instance.wrap("aa");

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

        assertTrue(aaa.equalsTo(aAA));
        assertFalse(aaa.equals(aa));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare("aaa", "aAA") == 0);
        assertFalse(instance.compare("aaa", "aa") < 0);
        assertFalse(instance.compare("aa", "aaA") > 0);

        assertTrue(aaa.compareTo(aAA) == 0);
        assertFalse(aaa.compareTo(aa) < 0);
        assertFalse(aa.compareTo(aAA) > 0);
    }

}