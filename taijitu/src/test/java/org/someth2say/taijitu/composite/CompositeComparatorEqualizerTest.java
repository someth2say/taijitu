package org.someth2say.taijitu.composite;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;
import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableEqualizable;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassThreeComparer;

public class CompositeComparatorEqualizerTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparer.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparer.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparer.equals(differentFrom1, differentFrom2));

        ComparableEqualizable<TestComposite> wrap1 = testClassThreeComparer.wrap(differentFrom1);
        assertTrue(wrap1.compareTo(differentFrom3) < 0);
        assertTrue(wrap1.compareTo(differentFrom2) == 0);
        assertTrue(wrap1.equals(testClassThreeComparer.wrap(differentFrom2)));

    }
}