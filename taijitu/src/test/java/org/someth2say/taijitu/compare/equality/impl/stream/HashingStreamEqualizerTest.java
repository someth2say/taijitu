package org.someth2say.taijitu.compare.equality.impl.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.compare.equality.impl.composite.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        TestComposite missingFrom1 = new TestComposite("bbb", "bbb", 2);
        TestComposite equalsFrom1 = new TestComposite("bBb", "bbb", 3);
        TestComposite equalsFrom2 = new TestComposite("bbb", "bbB", 3);

        Stream<TestComposite> stream1 = Stream.of(differentFrom1, missingFrom1, equalsFrom1);
        Stream<TestComposite> stream2 = Stream.of(differentFrom2, equalsFrom2);

        Missing<TestComposite> expectedMissing = new Missing<>(testClassThreeHasher, missingFrom1);
        Unequal<TestComposite> expectedDifference = new Unequal<>(testClassOneTwoEquality, differentFrom1, differentFrom2);

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality, testClassThreeHasher);
        List<Difference<?>> differences = streamEqualizer.underlyingDiffs(stream1, stream2).collect(Collectors.toList());

        // Test results
        assertEquals(2, differences.size());
        assertTrue(differences.contains(expectedMissing));
        assertTrue(differences.contains(expectedDifference));
    }

    @Test
    public void hashingStreamEqualizerInfiniteTest() {
        TestComposite differentTD1 = new TestComposite("", "", 100);
        TestComposite differentTD2 = new TestComposite("a", "a", 100);
        Unequal<TestComposite> expectedDifference = new Unequal<>(testClassOneTwoEquality, differentTD1, differentTD2);

        // Build Streams
        Stream<TestComposite> stream1 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i));
        Stream<TestComposite> stream2 = Stream.iterate(0, i -> i + 1).map(i -> new TestComposite("", "", i)).map(tc -> tc.equals(differentTD1) ? differentTD2 : tc);

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality, testClassThreeHasher);
        Stream<Difference<?>> differences = streamEqualizer.underlyingDiffs(stream1, stream2);

        // Test results
        Optional<Difference<?>> firstFound = differences.findAny();
        assertTrue(firstFound.isPresent());
        assertEquals(expectedDifference, firstFound.get());
    }
}
