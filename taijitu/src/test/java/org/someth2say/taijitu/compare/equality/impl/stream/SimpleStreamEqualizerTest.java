package org.someth2say.taijitu.compare.equality.impl.stream;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.impl.composite.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.stream.simple.SimpleStreamEqualizer;
import org.someth2say.taijitu.compare.explain.Difference;
import org.someth2say.taijitu.compare.explain.Missing;
import org.someth2say.taijitu.compare.explain.Unequal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassOneTwoEquality;

public class SimpleStreamEqualizerTest {
    @Test
    public void testSimpleStreamDifferences() {

        // Build Streams
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("aaa", "aa", 1);
        TestComposite equalsFrom1 = new TestComposite("bBb", "bbb", 2);
        TestComposite equalsFrom2 = new TestComposite("bbb", "bbB", 2);
        TestComposite missingFrom1 = new TestComposite("bbb", "bbb", 3);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, equalsFrom1, missingFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        SimpleStreamEqualizer<TestComposite> streamEquality
                = new SimpleStreamEqualizer<>(testClassOneTwoEquality);
        List<Difference> differences = streamEquality.explain(stream1, stream2).collect(Collectors.toList());

        // Test results
        differences.forEach(System.out::println);
        assertEquals(2, differences.size());
        assertTrue(differences.contains(new Missing<>(testClassOneTwoEquality, missingFrom1)));
        assertTrue(differences.contains(new Unequal<>(testClassOneTwoEquality, differentFrom1, differentFrom2)));

        //The following can be used to explain underlying differences
        // testClassOneTwoEquality.explain(differentFrom1,differentFrom2).forEach(System.out::println);
    }

    @Test
    public void testSimpleStreamEquals() {

        // Build Streams
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("aaa", "aa", 1);
        TestComposite equalsFrom1 = new TestComposite("bBb", "bbb", 3);
        TestComposite equalsFrom2 = new TestComposite("bbb", "bbB", 3);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, equalsFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        SimpleStreamEqualizer<TestComposite> streamEquality= new SimpleStreamEqualizer<>(testClassOneTwoEquality);
        assertFalse(streamEquality.areEquals(stream1, stream2));

    }
}
