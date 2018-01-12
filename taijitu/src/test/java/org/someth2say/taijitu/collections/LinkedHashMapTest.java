package org.someth2say.taijitu.collections;

import org.junit.Before;
import org.junit.Test;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;
import org.someth2say.taijitu.compare.equality.impl.value.NumberThreshold;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;

import java.util.*;

import static org.junit.Assert.*;

public class LinkedHashMapTest {

    LinkedHashMap<String, Float> hashMap;

    @Before
    public void init() {
        hashMap = new LinkedHashMap<>(new StringCaseInsensitive(), new NumberThreshold<>());
        hashMap.put("HOLA", 4f);
        hashMap.put("MUNDO", 5f);
    }

    @Test
    public void containsValue() {
        assertTrue(hashMap.containsValue(4f));
        assertFalse(hashMap.containsValue(3f));

        assertTrue(hashMap.containsValue(4.003f));
        assertFalse(hashMap.containsValue(4.02f));
    }

    @Test
    public void get() {
        assertEquals((Float) 4f, hashMap.get("hola"));
        assertNull(hashMap.get("adios"));
    }

    @Test
    public void getOrDefault() {
        assertEquals((Float) 4f, hashMap.getOrDefault("hola", 4f));
        assertEquals((Float) 4f, hashMap.getOrDefault("adios", 4f));
    }

    @Test
    public void clear() {
        assertFalse(hashMap.isEmpty());
        hashMap.clear();
        assertTrue(hashMap.isEmpty());
    }

    @Test
    public void keySet() {
        Set<String> keySet = hashMap.keySet();
        assertEquals(hashMap.size(), keySet.size());

        assertTrue(keySet.contains("hola"));
        assertTrue(keySet.contains("Mundo"));
        assertFalse(keySet.contains("adios"));

        //Order should be strict, but element "extracted" from the set do not retain equality
        Iterator<String> iterator = keySet.iterator();
        assertTrue(iterator.next().equals("HOLA"));
        assertTrue(iterator.next().equals("MUNDO"));

    }

    @Test
    public void values() {
        Collection<Float> values = hashMap.values();
        assertEquals(hashMap.size(), values.size());

        assertTrue(values.contains(4.0005f));
        assertTrue(values.contains(5f));
        assertFalse(values.contains(4.01f));

        //Order should be strict, but element "extracted" from the set do not retain equality
        Iterator<Float> iterator = values.iterator();
        assertTrue(iterator.next().equals(4f));
        assertTrue(iterator.next().equals(5f));
    }

    @Test
    public void entrySet() {
        Set<Map.Entry<String, Float>> entrySet = hashMap.entrySet();
        assertEquals(hashMap.size(), entrySet.size());
        LinkedHashMap.Entry<String, Float> holaEntry = new LinkedHashMap.Entry<>(0, "hola", 4.0001f, null, null, null);
        LinkedHashMap.Entry<String, Float> mundoEntry = new LinkedHashMap.Entry<>(0, "mundo", 5.0f, null, null, null);
        assertTrue(entrySet.contains(holaEntry));
        assertTrue(entrySet.contains(mundoEntry));
        assertTrue(entrySet.stream().noneMatch(entry -> entry.getKey().equals("bye")));
        assertTrue(entrySet.stream().noneMatch(entry -> entry.getValue().equals(4.01f)));

        // Check order is retained
        Iterator<Map.Entry<String, Float>> iterator = entrySet.iterator();
        assertTrue(iterator.next().equals(holaEntry)); // WARNING: Using assertEquals(holaEntry, iterator.next()) fails due to equality non-reflexiveness :'(
        assertTrue(iterator.next().equals(mundoEntry));

    }

    @Test
    public void forEach() {
        List<String> keys = new LinkedList<>();
        List<Float> values = new LinkedList<>();
        hashMap.forEach((k, v) -> {
            keys.add(k);
            values.add(v);
        });

        assertEquals(Arrays.asList("HOLA", "MUNDO"), keys);
        assertEquals(Arrays.asList(4f, 5f), values);

    }
}