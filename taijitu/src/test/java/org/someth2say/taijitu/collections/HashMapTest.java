package org.someth2say.taijitu.collections;

import org.junit.Before;
import org.junit.Test;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HashMapTest {

    private Hasher<String> hasher = new StringCaseInsensitive();
    private HashMap<String, Integer> hashMap;
    private Equalizer<Integer> equalizer = new JavaObject<>();

    @Before
    public void buildHolaMundoMap() {
        hashMap = new HashMap<>(hasher, equalizer);
        hashMap.put("Hola", 4);
        hashMap.put("mundo", 5);
    }

    @Test
    public void size() {
        assertEquals(2, hashMap.size());

        hashMap.put("hola", 2);
        hashMap.put("adios", 1);
        assertEquals(3, hashMap.size());
    }

    @Test
    public void isEmpty() {
        HashMap<String, Integer> hashMap = new HashMap<>(hasher, equalizer);
        assertTrue(hashMap.isEmpty());

        hashMap.put("hola", 4);
        assertFalse(hashMap.isEmpty());
    }

    @Test
    public void get() {

        assertEquals(new Integer(4), hashMap.get("Hola"));
        assertEquals(new Integer(4), hashMap.get("hola"));
        assertNull(hashMap.get("adios"));
    }

    @Test
    public void containsKey() {
        assertTrue(hashMap.containsKey("Hola"));
        assertTrue(hashMap.containsKey("hola"));
        assertFalse(hashMap.containsKey("adios"));
    }

    @Test
    public void put() {
        assertEquals(new Integer(4), hashMap.put("Hola", 5));
        assertEquals(new Integer(5), hashMap.put("HOLA", 4));
        assertEquals(null, hashMap.put("adios", 0));
    }

    @Test
    public void putAll() {
        Map<String, Integer> otherMap = new java.util.HashMap<String, Integer>() {{
            put("HOLA", -4);
            put("WORLD", -5);
        }};

        hashMap.putAll(otherMap);

        assertEquals(3, hashMap.size());
        assertEquals(new Integer(-4), hashMap.get("hola"));
        assertEquals(new Integer(5), hashMap.get("mundo"));
        assertEquals(new Integer(-5), hashMap.get("world"));
        assertNull(hashMap.get("adios"));
    }

    @Test
    public void remove() {

        assertEquals(new Integer(4), hashMap.remove("HOLA"));
        assertNull(hashMap.remove("ADIOS"));
        assertEquals(1, hashMap.size());

        assertFalse(hashMap.remove("MUNDO", 0));
        assertEquals(1, hashMap.size());
        assertTrue(hashMap.remove("MUNDO", 5));
    }

    @Test
    public void clear() {
        hashMap.clear();
        assertTrue(hashMap.isEmpty());
    }

    @Test
    public void containsValue() {
        assertTrue(hashMap.containsValue(5));
        assertFalse(hashMap.containsValue(0));
    }

    @Test
    public void keySet() {
        //TODO: KeySet should be also based on the same key equality
        Set<String> keySet = hashMap.keySet();
        keySet.containsAll(Arrays.asList("Hola", "mundo"));
    }

    @Test
    public void values() {
        Collection<Integer> values = hashMap.values();
        assertEquals(2, values.size());
        //TODO: Check value equality
        assertTrue(values.containsAll(Arrays.asList(4, 5)));
    }

    @Test
    public void entrySet() {
        Set<Map.Entry<String, Integer>> entrySet = hashMap.entrySet();
        assertEquals(2, entrySet.size());
        //TODO: Use hasher and equality
        assertTrue(
                entrySet.stream().anyMatch(entry -> (entry.getKey().equals("Hola") && entry.getValue().equals(4)))
                        &&
                        entrySet.stream().anyMatch(entry -> (entry.getKey().equals("mundo") && entry.getValue().equals(5)))
        );
    }

    @Test
    public void getOrDefault() {
        assertEquals(new Integer(4), hashMap.getOrDefault("HOLA", 0));
        assertEquals(new Integer(5), hashMap.getOrDefault("mundo", 0));
        assertEquals(new Integer(0), hashMap.getOrDefault("adios", 0));
    }

    @Test
    public void putIfAbsent() {
        assertEquals(new Integer(4), hashMap.putIfAbsent("HOLA", 0));
        assertEquals(new Integer(5), hashMap.putIfAbsent("mundo", 0));
        assertNull(hashMap.putIfAbsent("adios", 0));
    }

    @Test
    public void replace() {
        assertEquals(new Integer(4), hashMap.replace("HOLA", -4));
        assertEquals(new Integer(-4), hashMap.get("HOLA"));
        assertEquals(new Integer(5), hashMap.replace("mundo", -5));
        assertEquals(new Integer(-5), hashMap.get("mundo"));
        assertEquals(2, hashMap.size());

        assertFalse(hashMap.replace("hola", 6, -6));
        assertTrue(hashMap.replace("hola", -4, 4));
        assertTrue(hashMap.replace("mundo", -5, 5));
        assertEquals(2, hashMap.size());
    }

    @Test
    public void computeIfAbsent() {
        assertEquals(new Integer(4), hashMap.computeIfAbsent("HOLA", k -> k.length() + 1));
        assertEquals(new Integer(5), hashMap.computeIfAbsent("mundo", k -> k.length() + 1));
        assertEquals(2, hashMap.size());

        assertEquals(new Integer(6), hashMap.computeIfAbsent("adios", k -> k.length() + 1));
        assertEquals(3, hashMap.size());
        assertEquals(new Integer(6), hashMap.get("adios"));
    }

    @Test
    public void computeIfPresent() {
        assertEquals(new Integer(8), hashMap.computeIfPresent("HOLA", (k, v) -> k.length() + v));
        assertEquals(new Integer(8), hashMap.get("HOLA"));
        assertEquals(new Integer(10), hashMap.computeIfPresent("mundo", (k, v) -> k.length() + v));
        assertEquals(new Integer(10), hashMap.get("mundo"));
        assertEquals(2, hashMap.size());

        assertNull(hashMap.computeIfPresent("adios", (k, v) -> k.length() + v)); // Key not present
        assertEquals(2, hashMap.size());

        assertNull(hashMap.computeIfPresent("hola", (k, v) -> null));// Remap to null -> delete
        assertEquals(1, hashMap.size());
        assertNull(hashMap.get("HOLA"));
    }


    private static Integer addOrInvertLenght(String k, Integer v) {
        if (v != null) return k.length() + v;
        else return -k.length();
    }

    @Test
    public void compute() {
        hashMap.put("bye", null);
        // Key not present, new value null -> Do nothing & return null
        assertNull(hashMap.compute("adios", (k, v) -> null));
        assertNull(hashMap.get("adios"));
        assertEquals(3, hashMap.size());
        // Key not present, new value not null -> add
        assertEquals(new Integer(-5), hashMap.compute("adios", HashMapTest::addOrInvertLenght));
        assertEquals(new Integer(-5), hashMap.get("adios"));
        assertEquals(4, hashMap.size());

        // Key present, new value not null, Old value not null -> replace
        assertEquals(new Integer(8), hashMap.compute("HOLA", HashMapTest::addOrInvertLenght));
        assertEquals(new Integer(8), hashMap.get("HOLA"));
        assertEquals(4, hashMap.size());
        assertEquals(new Integer(10), hashMap.compute("mundo", HashMapTest::addOrInvertLenght));
        assertEquals(new Integer(10), hashMap.get("mundo"));
        assertEquals(4, hashMap.size());
        // Key present, new value not null, Old value null -> Replace
        assertEquals(new Integer(-3), hashMap.compute("BYE", HashMapTest::addOrInvertLenght));
        assertEquals(new Integer(-3), hashMap.get("BYE"));
        assertEquals(4, hashMap.size());
        // Key present, New value null -> Remove
        assertNull(hashMap.compute("BYE", (k, v) -> null));
        assertNull(hashMap.get("BYE"));
        assertEquals(3, hashMap.size());
    }

    @Test
    public void merge() {
        /**
         * If the specified key is not already associated with a value or is
         * associated with null, associates it with the given non-null value.
         */
        assertEquals(new Integer(11), hashMap.merge("hi", 11, Integer::sum));
        assertEquals(new Integer(11), hashMap.get("hi"));
        assertEquals(3, hashMap.size());
        hashMap.put("bye", null);
        assertEquals(new Integer(12), hashMap.merge("BYE", 12, Integer::sum));
        assertEquals(new Integer(12), hashMap.get("Bye"));
        assertEquals(4, hashMap.size());

        /**
         * Otherwise, replaces the associated value with the results of the given
         * remapping function...
         */
        assertEquals(new Integer(7), hashMap.merge("hola", 3, Integer::sum));
        assertEquals(new Integer(7), hashMap.get("HOLA"));
        assertEquals(4, hashMap.size());

        /**
         * ... or removes if the result is {@code null}.
         */
        assertNull(hashMap.merge("hola", 3, (x, y) -> null));
        assertNull(hashMap.get("HOLA"));
        assertEquals(3, hashMap.size());

    }
}