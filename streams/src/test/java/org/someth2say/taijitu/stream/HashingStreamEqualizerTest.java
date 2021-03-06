package org.someth2say.taijitu.stream;

import org.junit.Test;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.stream.mapping.HashingStreamEqualizer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HashingStreamEqualizerTest {


    @Test
    public void hashingStreamEqualizerTest() {
        // Build Streams
        TestComposite aFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite aFrom2 = new TestComposite("aaa", "aa", 1);
        TestComposite bFrom1a = new TestComposite("bBb", "bbb", 1);
        TestComposite bFrom1b = new TestComposite("bbb", "bbb", 1);
        TestComposite bFrom2 = new TestComposite("bbb", "bbB", 1);

        Stream<TestComposite> stream1 = Stream.of(aFrom1, bFrom1a, bFrom1b);
        Stream<TestComposite> stream2 = Stream.of(aFrom2, bFrom2);

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(TestComposite.testClassOneTwoEquality);
        List<Difference<TestComposite>> differences = streamEqualizer.explain(stream1, stream2).collect(Collectors.toList());

        differences.forEach(System.out::println);
        // Test results
        assertEquals(3, differences.size());
        assertTrue(differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, aFrom1)));
        assertTrue(differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, aFrom2)));
        assertTrue(differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, bFrom1a))|differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, bFrom1b)));
    }

    @Test
    public void hashingStreamEqualizerTest_colisions_same_stream() {
        // Build Streams
        TestComposite a1 = new TestComposite("aaa", "aaa", 1);
        TestComposite a2 = new TestComposite("aaa", "aaA", 1);

        Stream<TestComposite> stream1 = Stream.of(a1,a2);
        Stream<TestComposite> stream2 = Stream.empty();

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(TestComposite.testClassOneTwoEquality);
        List<Difference<TestComposite>> differences = streamEqualizer.explain(stream1, stream2).collect(Collectors.toList());

        differences.forEach(System.out::println);
        // Test results
        assertEquals(2, differences.size());
        assertTrue(differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, a1)));
        assertTrue(differences.contains(new Missing<>(TestComposite.testClassOneTwoEquality, a2)));
    }

/*    @Test(timeout = 1000)
    public void hashingStreamEqualizerInfiniteTest() {
        TestComposite differentTD1 = new TestComposite("", "", 100);
        TestComposite differentTD2 = new TestComposite("a", "b", 100);

        // Build Streams
        Stream<TestComposite> stream1 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i));
        Stream<TestComposite> stream2 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i)).map(tc -> tc.equals(differentTD1) ? differentTD2 : tc);

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality);
        List<Difference> differences = streamEqualizer.explain(stream1, stream2).limit(1).collect(Collectors.toList());

        assertEquals(1, differences.size());
        // Test results
        assertTrue(differences.contains(new Unequal<>(testClassOneTwoEquality, differentTD1, differentTD2)));
    }*/
}
