package org.someth2say.taijitu.composite;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.wrapper.AbstractWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableWraper;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassThreeComparer;

public class CompositeComparatorTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparer.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparer.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparer.equals(differentFrom1, differentFrom2));

        IComparableWraper<TestComposite, ?> wrap1 = testClassThreeComparer.wrap(differentFrom1);
        IComparableWraper<TestComposite, ?> wrap3 = testClassThreeComparer.wrap(differentFrom3);
        IComparableWraper<TestComposite, ?> wrap2 = testClassThreeComparer.wrap(differentFrom2);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

    }
}