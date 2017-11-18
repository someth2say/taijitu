package org.someth2say.taijitu.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamUtilTest {
    @Test
    public void zip() throws Exception {
        Stream<String> s1 = Stream.of("a", "b", "c", "d");
        Stream<String> s2 = Stream.of("1", "2", "3");
        Stream<String> zip = StreamUtil.zip(s1, s2, String::concat);
        assertEquals(zip.collect(Collectors.toList()), Arrays.asList("a1", "b2", "c3"));
    }

    @Test
    public void zipWithTail() throws Exception {
        Stream<String> s1 = Stream.of("a", "b", "c", "d");
        Stream<String> s2 = Stream.of("1", "2", "3");
        Stream<String> zip = StreamUtil.zip(s1, s2, String::concat, String::toUpperCase, Function.identity());
        assertEquals(zip.collect(Collectors.toList()), Arrays.asList("a1", "b2", "c3", "D"));


    }

}