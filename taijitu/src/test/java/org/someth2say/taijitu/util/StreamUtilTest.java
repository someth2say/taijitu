package org.someth2say.taijitu.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamUtilTest {
    @Test
    public void zip() {
        Stream<String> s1 = Stream.of("a", "b", "c", "d");
        Stream<String> s2 = Stream.of("1", "2", "3");
        Stream<String> zip = StreamUtil.biMap(s1, s2, String::concat);
        assertEquals(Arrays.asList("a1", "b2", "c3"), zip.collect(Collectors.toList()));
    }

    @Test
    public void zipWithTail() {
        Stream<String> s1 = Stream.of("a", "b", "c", "d");
        Stream<String> s2 = Stream.of("1", "2", "3");
        Stream<String> zip = StreamUtil.biMapTail(s1, s2, String::concat, String::toUpperCase, Function.identity());
        assertEquals(Arrays.asList("a1", "b2", "c3", "D"), zip.collect(Collectors.toList()));
    }


    @Test
    public void steppingBiMapTailTest() {
        Stream<String> s1 = Stream.of("a", "c", "d");
        Stream<String> s2 = Stream.of("a", "b", "c", "d");

        Stream<String> stream = StreamUtil.steppingBiMapTail(s1, s2, String::concat, String::compareTo, Function.identity(), Function.identity());
        List<String> collect = stream.collect(Collectors.toList());
        System.out.println(StringUtils.join(collect,","));
        assertEquals(Arrays.asList("aa", "b", "cc", "dd"), collect);
    }

}