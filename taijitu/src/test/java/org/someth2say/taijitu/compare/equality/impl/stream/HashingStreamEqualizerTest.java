package org.someth2say.taijitu.compare.equality.impl.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.compare.equality.impl.composite.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.stream.mapping.HashingStreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassOneTwoEquality;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeHasher;

@RunWith(Parameterized.class)
public class HashingStreamEqualizerTest {

    private final boolean parallel;

    @Parameterized.Parameters(name = "Parallel: {0}")
    public static Collection<Boolean> fields() {
        return Arrays.asList(
                true, false
        );
    }

    public HashingStreamEqualizerTest(boolean parallel) {
        this.parallel = parallel;
    }

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

        HashingStreamEqualizer<TestComposite> streamEqualizer = new HashingStreamEqualizer<>(testClassOneTwoEquality, testClassThreeHasher);
        streamEqualizer.setParallel(parallel);
        List<Difference<?>> differences = streamEqualizer.underlyingDiffs(stream1, stream2).collect(Collectors.toList());

        // Test results
        Missing<TestComposite> missing = new Missing<>(testClassThreeHasher, missingFrom1);
        assertEquals(2, differences.size());
        assertTrue(differences.contains(missing));
        Unequal<TestComposite> unequal = new Unequal<>(testClassOneTwoEquality, differentFrom1, differentFrom2);
        assertTrue(differences.contains(unequal));
    }
}
