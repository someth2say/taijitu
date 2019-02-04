package org.someth2say.taijitu.compare.equality.impl.stream;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.impl.composite.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.compare.explain.Difference;
import org.someth2say.taijitu.compare.explain.Missing;
import org.someth2say.taijitu.compare.explain.Unequal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassOneTwoEquality;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeHasher;

public class HashingStreamEqualizerTest {


    @Test
    public void hashingStreamEqualizerTest() {
        // Build Streams
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("aaa", "aa", 1);
        TestComposite equalsFrom1 = new TestComposite("bBb", "bbb", 3);
        TestComposite equalsFrom2 = new TestComposite("bbb", "bbB", 3);
        TestComposite missingFrom1 = new TestComposite("bbb", "bbb", 2);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, equalsFrom1, missingFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        //TODO: Should not be using a different hasher!!
        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality, testClassThreeHasher);
//        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality);
        List<Difference> differences = streamEqualizer.explain(stream1, stream2).collect(Collectors.toList());

        differences.forEach(System.out::println);
        // Test results
        assertEquals(2, differences.size());
        assertTrue(differences.contains(new Missing<>(testClassThreeHasher, missingFrom1)));
        assertTrue(differences.contains(new Unequal<>(testClassOneTwoEquality, differentFrom1, differentFrom2)));
    }

    @Test(timeout = 1000)
    public void hashingStreamEqualizerInfiniteTest() {
        TestComposite differentTD1 = new TestComposite("", "", 100);
        TestComposite differentTD2 = new TestComposite("a", "b", 100);

        // Build Streams
        Stream<TestComposite> stream1 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i));
        Stream<TestComposite> stream2 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i)).map(tc -> tc.equals(differentTD1) ? differentTD2 : tc);

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality, testClassThreeHasher);
        List<Difference> differences = streamEqualizer.explain(stream1, stream2).limit(1).collect(Collectors.toList());

        assertEquals(1, differences.size());
        // Test results
        assertTrue(differences.contains(new Unequal<>(testClassOneTwoEquality, differentTD1, differentTD2)));
    }
}
