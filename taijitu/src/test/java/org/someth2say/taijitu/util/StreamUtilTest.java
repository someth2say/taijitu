package org.someth2say.taijitu.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static org.junit.Assert.assertEquals;
import static org.someth2say.taijitu.util.StreamUtil.*;

public class StreamUtilTest {
    @Test
    public void zip() {
        Stream<String> s1 = of("a", "b", "c", "d");
        Stream<String> s2 = of("1", "2", "3");
        Stream<String> zip = biMap(s1, s2, String::concat);
        assertEquals(Arrays.asList("a1", "b2", "c3"), zip.collect(toList()));
    }

    @Test
    public void zipWithTail() {
        Stream<String> s1 = of("a", "b", "c", "d");
        Stream<String> s2 = of("1", "2", "3");
        Stream<String> zip = biMapTail(s1, s2, String::concat, String::toUpperCase, identity());
        assertEquals(Arrays.asList("a1", "b2", "c3", "D"), zip.collect(toList()));
    }

    @Test
    public void steppingBiMapTailTest() {

        List<String> collect;
        collect = steppingBiMapTail(of("-","a", "c", "d","e"), of("a", "b", "c", "d"), String::concat, String::compareTo, identity(), identity()).collect(toList());
        assertEquals(Arrays.asList("-","aa", "b", "cc", "dd","e"), collect);
        collect = steppingBiMapTail(of("a", "c", "d"), empty(), String::concat, String::compareTo, identity(), identity()).collect(toList());
        assertEquals(Arrays.asList("a", "c", "d"), collect);
        collect = steppingBiMapTail(empty(), of("a", "c", "d"), String::concat, String::compareTo, identity(), identity()).collect(toList());
        assertEquals(Arrays.asList("a", "c", "d"), collect);

    }

}