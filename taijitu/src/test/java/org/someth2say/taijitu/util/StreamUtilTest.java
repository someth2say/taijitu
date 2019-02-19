package org.someth2say.taijitu.util;

import org.junit.Test;
import org.someth2say.taijitu.stream.StreamUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static org.junit.Assert.assertEquals;
import static org.someth2say.taijitu.stream.StreamUtil.*;

public class StreamUtilTest {

    @Test
    public void zipTest() {
        Stream<String> s1 = of("a", "b", "c");
        Stream<String> s2 = of("1", "2", "3", "4");
        Stream<String> zip = zip(s1, s2, 2, false);
        assertEquals(Arrays.asList("a", "b", "1", "2", "c"), zip.collect(toList()));
    }

    @Test
    public void zipTest2() {
        Stream<String> s1 = of("a", "b", "c");
        Stream<String> s2 = of("1", "2", "3", "4");
        Stream<String> zip = zip(s2, s1, 2, false);
        assertEquals(Arrays.asList("1", "2", "a", "b", "3", "4", "c"), zip.collect(toList()));
    }

    @Test
    public void zipTailTest() {
        Stream<String> s1 = of("a", "b", "c");
        Stream<String> s2 = of("1", "2", "3", "4");
        Stream<String> zip = zip(s1, s2, 2, true);
        assertEquals(Arrays.asList("a", "b", "1", "2", "c", "3", "4"), zip.collect(toList()));
    }

    @Test
    public void zipTailTest2() {
        Stream<String> s1 = of("a", "b", "c");
        Stream<String> s2 = of("1", "2", "3", "4");
        Stream<String> zip = zip(s2, s1, 2, true);
        assertEquals(Arrays.asList("1", "2", "a", "b", "3", "4", "c"), zip.collect(toList()));
    }

    @Test
    public void biMapTest() {
        Stream<String> s1 = of("a", "b", "c", "d");
        Stream<String> s2 = of("1", "2", "3");
        Stream<String> zip = biMap(s1, s2, String::concat);
        assertEquals(Arrays.asList("a1", "b2", "c3"), zip.collect(toList()));
    }

    @Test
    public void biMapTailTest() {
        Stream<String> s1 = of("a", "b", "c", "d");
        Stream<String> s2 = of("1", "2", "3");
        Stream<String> zip = biMapTail(s1, s2, String::concat, String::toUpperCase, identity());
        assertEquals(Arrays.asList("a1", "b2", "c3", "D"), zip.collect(toList()));
    }

    @Test
    public void comparingBiMapTest() {
        List<String> collect;
        BiPredicate<? super String, ? super String> biFilter = String::equals;
        collect = StreamUtil.comparingBiMap(of("-", "a", "C", "d", "e"), of("a", "b", "c", "D"), String::compareToIgnoreCase, String::concat, String::toUpperCase, biFilter).collect(toList());
        assertEquals(Arrays.asList("-", "B", "Cc", "dD", "E"), collect);
        collect = StreamUtil.comparingBiMap(of("a", "c", "d"), empty(), String::compareToIgnoreCase, String::concat, identity(), biFilter).collect(toList());
        assertEquals(Arrays.asList("a", "c", "d"), collect);
        collect = StreamUtil.comparingBiMap(empty(), of("a", "c", "d"), String::compareToIgnoreCase, String::concat, identity(), biFilter).collect(toList());
        assertEquals(Arrays.asList("a", "c", "d"), collect);
    }

}