package org.someth2say.taijitu.compare.equality.impl.stream;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.impl.composite.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.stream.sorted.ComparableStreamEqualizer;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassOneTwoEquality;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeComparator;

public class ComparableStreamEqualizerTest {
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

        ComparableStreamEqualizer<TestComposite> equality = new ComparableStreamEqualizer<>(testClassOneTwoEquality, testClassThreeComparator);
        List<Difference> differences = equality.underlyingDiffs(stream1, stream2).collect(Collectors.toList());

        // Test results
        differences.forEach(System.out::println);
        Missing<TestComposite> missing = new Missing<>(testClassThreeComparator, missingFrom1);
        assertEquals(2, differences.size());
        assertTrue(differences.contains(missing));
//        Stream<Difference<?>> underlyingCauses = Stream.of(new Unequal<>(new StringCaseInsensitive(), "aaa", "aa"));
        Unequal<TestComposite> unequal = new Unequal<>(testClassOneTwoEquality, differentFrom1, differentFrom2);
        assertTrue(differences.contains(unequal));
    }
}
