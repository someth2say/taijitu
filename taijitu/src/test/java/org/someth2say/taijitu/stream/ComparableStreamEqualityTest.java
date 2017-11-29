package org.someth2say.taijitu.stream;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;
import org.someth2say.taijitu.compare.equality.value.StringCaseInsensitive;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassOneTwoEquality;
import static org.someth2say.taijitu.TestComposite.testClassThreeComparer;

public class ComparableStreamEqualityTest {
    @Test
    public void testComparableStreamEquality() {
        // Build Streams
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("aaa", "aa", 1);
        TestComposite missingFrom1 = new TestComposite("bbb", "bbb", 2);
        TestComposite equalsFrom1 = new TestComposite("bBb", "bbb", 3);
        TestComposite equalsFrom2 = new TestComposite("bbb", "bbB", 3);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, missingFrom1, equalsFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        ComparableStreamEquality<TestComposite> equality = new ComparableStreamEquality<>(testClassOneTwoEquality, testClassThreeComparer);
        List<Mismatch<?>> mismatches = equality.underlyingDiffs(stream1, stream2);

        // Test results
        mismatches.forEach(System.out::println);
        Missing<TestComposite> missing = new Missing<>(testClassThreeComparer, missingFrom1);
        assertEquals(2, mismatches.size());
        assertTrue(mismatches.contains(missing));
        List<Mismatch<?>> underlyingCauses = Collections.singletonList(new Difference<>(new StringCaseInsensitive(), "aaa", "aa"));
        Difference<TestComposite> difference = new Difference<>(testClassOneTwoEquality, differentFrom1, differentFrom2, underlyingCauses);
        assertTrue(mismatches.contains(difference));
    }
}
