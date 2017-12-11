package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassOneTwoEquality;

public class CompositeEqualizerTest {
    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertFalse(testClassOneTwoEquality.equals(differentFrom1, differentFrom2));
        assertTrue(testClassOneTwoEquality.equals(differentFrom1, differentFrom3));
        assertTrue(testClassOneTwoEquality.underlyingDiffs(differentFrom1, differentFrom2)
                .containsAll(Arrays.asList(
                        new Unequal<>(new StringCaseInsensitive(), "aaa", "bbb"),
                        new Unequal<>(new StringCaseInsensitive(), "aaa", "ccc"))));

        assertFalse(testClassOneTwoEquality.wrap(differentFrom1).equals(testClassOneTwoEquality.wrap(differentFrom2)));
        assertTrue(testClassOneTwoEquality.wrap(differentFrom1).equals(testClassOneTwoEquality.wrap(differentFrom3)));

    }
}
