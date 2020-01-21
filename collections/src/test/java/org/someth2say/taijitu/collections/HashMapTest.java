package org.someth2say.taijitu.collections;

import org.junit.Before;
import org.junit.Test;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.impl.value.NumberThresholdComparatorHasher;
import org.someth2say.taijitu.equality.impl.value.StringCaseInsensitiveComparatorHasher;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HashMapTest {

    private Hasher<String> hasher = StringCaseInsensitiveComparatorHasher.INSTANCE;
    private HashMap<String, Float> hashMap;
    private Equalizer<Number> equalizer = NumberThresholdComparatorHasher.INSTANCE;

    @Before
    public void buildHolaMundoMap() {
        hashMap = new HashMap<>(hasher, equalizer);
        hashMap.put("Hola", 4f);
        hashMap.put("mundo", 5f);
    }

    @Test
    public void size() {
        assertEquals(2, hashMap.size());

        hashMap.put("hola", 2f);
        hashMap.put("adios", 1f);
        assertEquals(3, hashMap.size());
    }

    @Test
    public void isEmpty() {
        HashMap<String, Float> hashMap = new HashMap<>(hasher, equalizer);
        assertTrue(hashMap.isEmpty());

        hashMap.put("hola", 4f);
        assertFalse(hashMap.isEmpty());
    }

    @Test
    public void get() {

        assertEquals((Float) 4f, hashMap.get("Hola"));
        assertEquals((Float) 4f, hashMap.get("hola"));
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
        assertEquals((Float) 4f, hashMap.put("Hola", 5f));
        assertEquals((Float) 5f, hashMap.put("HOLA", 4f));
        assertEquals(null, hashMap.put("adios", 0f));
    }

    @Test
    public void putAll() {
        Map<String, Float> otherMap = new java.util.HashMap<String, Float>() {
            /**
            *
            */
            private static final long serialVersionUID = 1L;

            {
            put("HOLA", -4f);
            put("WORLD", -5f);
        }};

        hashMap.putAll(otherMap);

        assertEquals(3, hashMap.size());
        assertEquals((Float) (-4f), hashMap.get("hola"));
        assertEquals((Float) (5f), hashMap.get("mundo"));
        assertEquals((Float) (-5f), hashMap.get("world"));
        assertNull(hashMap.get("adios"));
    }

    @Test
    public void remove() {

        assertEquals((Float) (4f), hashMap.remove("HOLA"));
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
        assertTrue(hashMap.containsValue(5f));
        assertTrue(hashMap.containsValue(5.0006f));
        assertFalse(hashMap.containsValue(0f));
    }

    @Test
    public void keySet() {
        Set<String> keySet = hashMap.keySet();
        assertEquals(hashMap.size(), keySet.size());
        assertTrue(keySet.contains("HOLA"));
        assertTrue(keySet.contains("MUNDO"));
        assertFalse(keySet.contains("adios"));
    }

    @Test
    public void values() {
        Collection<Float> values = hashMap.values();
        assertEquals(hashMap.size(), values.size());
        assertEquals(hashMap.size(), values.size());
        assertTrue(values.contains(4.0005f));
        assertTrue(values.contains(5f));
        assertFalse(values.contains(4.01f));
    }

    @Test
    public void entrySet() {
        Set<Map.Entry<String, Float>> entrySet = hashMap.entrySet();
        assertEquals(hashMap.size(), entrySet.size());
        LinkedHashMap.Entry<String, Float> holaEntry = new LinkedHashMap.Entry<>(0, "hola", 4.0001f, null, null, null);
        LinkedHashMap.Entry<String, Float> mundoEntry = new LinkedHashMap.Entry<>(0, "hola", 4.0001f, null, null, null);
        assertTrue(entrySet.contains(holaEntry));
        assertTrue(entrySet.contains(mundoEntry));
        assertTrue(entrySet.stream().noneMatch(entry -> entry.getKey().equals("bye")));
        assertTrue(entrySet.stream().noneMatch(entry -> entry.getValue().equals(4.01f)));
    }

    @Test
    public void getOrDefault() {
        assertEquals((Float) (4f), hashMap.getOrDefault("HOLA", 0f));
        assertEquals((Float) (5f), hashMap.getOrDefault("mundo", 0f));
        assertEquals((Float) (0f), hashMap.getOrDefault("adios", 0f));
    }

    @Test
    public void putIfAbsent() {
        assertEquals((Float) (4f), hashMap.putIfAbsent("HOLA", 0f));
        assertEquals((Float) (5f), hashMap.putIfAbsent("mundo", 0f));
        assertNull(hashMap.putIfAbsent("adios", 0f));
    }

    @Test
    public void replace() {
        assertEquals((Float) (4f), hashMap.replace("HOLA", -4f));
        assertEquals((Float) (-4f), hashMap.get("HOLA"));
        assertEquals((Float) (5f), hashMap.replace("mundo", -5f));
        assertEquals((Float) (-5f), hashMap.get("mundo"));
        assertEquals(2, hashMap.size());

        assertFalse(hashMap.replace("hola", 6f, -6f));
        assertTrue(hashMap.replace("hola", -4f, 4f));
        assertTrue(hashMap.replace("mundo", -5f, 5f));
        assertEquals(2, hashMap.size());
    }

    @Test
    public void computeIfAbsent() {
        assertEquals((Float) (4f), hashMap.computeIfAbsent("HOLA", k -> (float) k.length() + 1));
        assertEquals((Float) (5f), hashMap.computeIfAbsent("mundo", k -> (float) k.length() + 1));
        assertEquals(2, hashMap.size());

        assertEquals((Float) (6f), hashMap.computeIfAbsent("adios", k -> (float) k.length() + 1));
        assertEquals(3, hashMap.size());
        assertEquals((Float) (6f), hashMap.get("adios"));
    }

    @Test
    public void computeIfPresent() {
        assertEquals((Float) (8f), hashMap.computeIfPresent("HOLA", (k, v) -> k.length() + v));
        assertEquals((Float) (8f), hashMap.get("HOLA"));
        assertEquals((Float) (10f), hashMap.computeIfPresent("mundo", (k, v) -> k.length() + v));
        assertEquals((Float) (10f), hashMap.get("mundo"));
        assertEquals(2, hashMap.size());

        assertNull(hashMap.computeIfPresent("adios", (k, v) -> k.length() + v)); // Key not present
        assertEquals(2, hashMap.size());

        assertNull(hashMap.computeIfPresent("hola", (k, v) -> null));// Remap to null -> delete
        assertEquals(1, hashMap.size());
        assertNull(hashMap.get("HOLA"));
    }


    private static Float addOrInvertLenght(String k, Float v) {
        if (v != null) return k.length() + v;
        else return (float) -k.length();
    }

    @Test
    public void compute() {
        hashMap.put("bye", null);
        // Key not present, new value null -> Do nothing & return null
        assertNull(hashMap.compute("adios", (k, v) -> null));
        assertNull(hashMap.get("adios"));
        assertEquals(3, hashMap.size());
        // Key not present, new value not null -> add
        assertEquals((Float) (-5f), hashMap.compute("adios", HashMapTest::addOrInvertLenght));
        assertEquals((Float) (-5f), hashMap.get("adios"));
        assertEquals(4, hashMap.size());

        // Key present, new value not null, Old value not null -> replace
        assertEquals((Float) (8f), hashMap.compute("HOLA", HashMapTest::addOrInvertLenght));
        assertEquals((Float) (8f), hashMap.get("HOLA"));
        assertEquals(4, hashMap.size());
        assertEquals((Float) (10f), hashMap.compute("mundo", HashMapTest::addOrInvertLenght));
        assertEquals((Float) (10f), hashMap.get("mundo"));
        assertEquals(4, hashMap.size());
        // Key present, new value not null, Old value null -> Replace
        assertEquals((Float) (-3f), hashMap.compute("BYE", HashMapTest::addOrInvertLenght));
        assertEquals((Float) (-3f), hashMap.get("BYE"));
        assertEquals(4, hashMap.size());
        // Key present, New value null -> Remove
        assertNull(hashMap.compute("BYE", (k, v) -> null));
        assertNull(hashMap.get("BYE"));
        assertEquals(3, hashMap.size());
    }

    @Test
    public void merge() {
        /*
         * If the specified key is not already associated with a value or is
         * associated with null, associates it with the given non-null value.
         */
        assertEquals((Float) (11f), hashMap.merge("hi", 11f, Float::sum));
        assertEquals((Float) (11f), hashMap.get("hi"));
        assertEquals(3, hashMap.size());
        hashMap.put("bye", null);
        assertEquals((Float) (12f), hashMap.merge("BYE", 12f, Float::sum));
        assertEquals((Float) (12f), hashMap.get("Bye"));
        assertEquals(4, hashMap.size());

        /*
         * Otherwise, replaces the associated value with the results of the given
         * remapping function...
         */
        assertEquals((Float) (7f), hashMap.merge("hola", 3f, Float::sum));
        assertEquals((Float) (7f), hashMap.get("HOLA"));
        assertEquals(4, hashMap.size());

        /*
         * ... or removes if the explain is {@code null}.
         */
        assertNull(hashMap.merge("hola", 3f, (x, y) -> null));
        assertNull(hashMap.get("HOLA"));
        assertEquals(3, hashMap.size());
    }
}