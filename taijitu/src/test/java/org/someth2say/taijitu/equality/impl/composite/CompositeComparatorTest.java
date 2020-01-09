package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.ComparableWrapper;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassThreeComparator;

public class CompositeComparatorTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparator.areEquals(differentFrom1, differentFrom2));

        ComparableWrapper<TestComposite> wrap1 = new ComparableWrapper<>(differentFrom1, testClassThreeComparator);
        ComparableWrapper<TestComposite> wrap3 = new ComparableWrapper<>(differentFrom3, testClassThreeComparator);
        ComparableWrapper<TestComposite> wrap2 = new ComparableWrapper<>(differentFrom2, testClassThreeComparator);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

    }
}