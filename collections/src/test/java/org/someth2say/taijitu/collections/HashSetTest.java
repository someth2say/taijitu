package org.someth2say.taijitu.collections;

import org.junit.Before;
import org.junit.Test;
import org.someth2say.taijitu.equality.impl.value.StringCaseInsensitive;

import static org.junit.Assert.*;

public class HashSetTest {

    private HashSet<String> hashSet;

    @Before
    public void buildHashSet() {
        hashSet = new HashSet<>(StringCaseInsensitive.EQUALITY);
        hashSet.add("HOLA");
        hashSet.add("MUNDO");
    }

    @Test
    public void size() {
        assertEquals(2, hashSet.size());
        hashSet.clear();
        assertEquals(0, hashSet.size());
    }

    @Test
    public void isEmpty() {
        assertFalse(hashSet.isEmpty());
        hashSet.clear();
        assertTrue(hashSet.isEmpty());
    }

    @Test
    public void contains() {
        assertTrue(hashSet.contains("HOLA"));
        assertTrue(hashSet.contains("mundo"));
        assertFalse(hashSet.contains("bye"));
    }

    @Test
    public void add() {
        assertTrue(hashSet.contains("hola"));
        assertEquals(2, hashSet.size());
        hashSet.add("Hola");
        assertTrue(hashSet.contains("hola"));
        assertEquals(2, hashSet.size());
    }

    @Test
    public void remove() {
        assertTrue(hashSet.remove("Hola"));
        assertEquals(1, hashSet.size());
        assertFalse(hashSet.contains("HOLA"));

        assertFalse(hashSet.remove("bye"));
        assertEquals(1, hashSet.size());
        assertFalse(hashSet.contains("bye"));

    }

    @Test
    public void clear() {
        assertEquals(2, hashSet.size());
        hashSet.clear();
        assertTrue(hashSet.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void _clone() {
        HashSet<String> clone = (HashSet<String>) hashSet.clone();
        assertTrue(clone.contains("hola"));
        assertTrue(clone.contains("HOLA"));
        assertFalse(clone.contains("bye"));
    }
}