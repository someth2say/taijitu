package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableHashableWrapper;

import static org.junit.Assert.*;

public class StringCaseInsensitiveTest {

    final StringCaseInsensitive instance = new StringCaseInsensitive();
    final IComparableHashableWrapper<String, ?> aaa = new ComparableHashableWrapper<>("aaa", instance);
    final IComparableHashableWrapper<String, ?> aAA = new ComparableHashableWrapper<>("aAA", instance);
    final IComparableHashableWrapper<String, ?> aa = new ComparableHashableWrapper<>("aa", instance);

    @Test
    public void hashCodeTest() {
        assertEquals(instance.hash("aaa"), instance.hash("aAA"));
        assertNotEquals(instance.hash("aaa"), instance.hash("aa"));

        assertEquals(aaa.hashCode(), instance.hash("aaa"));

    }

    @Test
    public void equalsTest() {
        assertTrue(instance.areEquals("aaa", "aAA"));
        assertFalse(instance.areEquals("aaa", "aa"));

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