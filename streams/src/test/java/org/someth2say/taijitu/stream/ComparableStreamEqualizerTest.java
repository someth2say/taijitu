package org.someth2say.taijitu.stream;

import org.junit.Test;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.stream.sorted.ComparableStreamEqualizer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.stream.TestComposite.testClassOneTwoEquality;

public class ComparableStreamEqualizerTest {
    @Test
    public void testComparableStreamEquality() {
        // Build Streams
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("aaa", "aaA", 1);
        TestComposite missingFrom1 = new TestComposite("bbb", "bbb", 2);
        TestComposite equalsFrom1 = new TestComposite("cCc", "ccc", 3);
        TestComposite equalsFrom2 = new TestComposite("ccc", "ccC", 3);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, missingFrom1, equalsFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        ComparableStreamEqualizer<TestComposite> equality = new ComparableStreamEqualizer<>(testClassOneTwoEquality);
        List<Difference<TestComposite>> differences = equality.explain(stream1, stream2).collect(Collectors.toList());

        // Test results
        differences.forEach(System.out::println);
        assertEquals(1, differences.size());
        assertTrue(differences.contains(new Missing<>(testClassOneTwoEquality, missingFrom1)));
    }
}
