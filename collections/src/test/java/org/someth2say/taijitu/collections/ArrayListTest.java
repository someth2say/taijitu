package org.someth2say.taijitu.collections;

import org.junit.Test;
import org.someth2say.taijitu.equality.impl.value.StringCaseInsensitiveComparatorHasher;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ArrayListTest {

    @Test
    public void _indexOf() {
        ArrayList<String> insensitive = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "hola", "mundo"));
        assertEquals(0, insensitive.indexOf("HOLA"));
        assertEquals(2, insensitive.indexOf("mundo"));
        assertEquals(-1, insensitive.indexOf("adios"));

        ArrayList<String> sensitive = new ArrayList<>(Arrays.asList("Hola", "hola", "mundo"));
        assertEquals(-1, sensitive.indexOf("HOLA"));
        assertEquals(2, sensitive.indexOf("mundo"));
        assertEquals(-1, sensitive.indexOf("adios"));
    }

    @Test
    public void _lastIndexOf() {
        ArrayList<String> insensitive = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "hola", "mundo"));
        assertEquals(1, insensitive.lastIndexOf("HOLA"));
        assertEquals(2, insensitive.lastIndexOf("mundo"));
        assertEquals(-1, insensitive.indexOf("adios"));

        ArrayList<String> sensitive = new ArrayList<>(Arrays.asList("Hola", "hola", "mundo"));
        assertEquals(-1, sensitive.lastIndexOf("HOLA"));
        assertEquals(2, sensitive.lastIndexOf("mundo"));
        assertEquals(-1, sensitive.indexOf("adios"));
    }

    @Test
    public void _clone() {
        ArrayList<String> insensitive = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "hola", "mundo"));
        Object clone = insensitive.clone();
        assertEquals(insensitive, clone);
    }

    @Test
    public void _remove() {
        ArrayList<String> insensitive = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "hola", "mundo"));
        @SuppressWarnings("unchecked")
        ArrayList<String> clone = (ArrayList<String>) insensitive.clone();

        assertTrue(insensitive.remove("HOLA"));
        clone.remove(0);

        assertEquals(clone, insensitive);

        ArrayList<String> sensitive = new ArrayList<>(Arrays.asList("Hola", "hola", "mundo"));
        assertFalse(sensitive.remove("HOLA"));
        assertTrue(sensitive.remove("hola"));
    }

    @Test
    public void _equals() {
        ArrayList<String> insensitive1 = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "hola", "mundo"));
        ArrayList<String> insensitive2 = new ArrayList<>(StringCaseInsensitiveComparatorHasher.INSTANCE, Arrays.asList("Hola", "HOLA", "mundo"));
        assertEquals(insensitive1, insensitive2);

        ArrayList<String> sensitive1 = new ArrayList<>(Arrays.asList("Hola", "hola", "mundo"));
        ArrayList<String> sensitive2 = new ArrayList<>(Arrays.asList("Hola", "HOLA", "mundo"));
        assertNotEquals(sensitive1, sensitive2);
    }

}