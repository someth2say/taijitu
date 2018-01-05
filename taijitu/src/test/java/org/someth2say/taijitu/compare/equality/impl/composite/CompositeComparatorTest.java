package org.someth2say.taijitu.compare.equality.impl.composite;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeComparator;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableWraper;

public class CompositeComparatorTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparator.equals(differentFrom1, differentFrom2));

        // TODO: Those casts seems only to be needed in Java8 ...
        IComparableWraper<TestComposite, ?> wrap1 = (IComparableWraper<TestComposite, ?>) testClassThreeComparator.wrap(differentFrom1);
        IComparableWraper<TestComposite, ?> wrap3 = (IComparableWraper<TestComposite, ?>) testClassThreeComparator.wrap(differentFrom3);
        IComparableWraper<TestComposite, ?> wrap2 = (IComparableWraper<TestComposite, ?>) testClassThreeComparator.wrap(differentFrom2);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

    }
}