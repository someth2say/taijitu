package org.someth2say.taijitu.compare.equality.impl.composite;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeComparator;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableWrapper;

public class CompositeComparatorTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparator.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparator.areEquals(differentFrom1, differentFrom2));

        // TODO: Those casts seems only to be needed in Java8 ...
        ComparableWrapper<TestComposite,?> wrap1 = (ComparableWrapper<TestComposite,?>) new ComparableWrapper<>(differentFrom1, testClassThreeComparator);
        ComparableWrapper<TestComposite,?> wrap3 = (ComparableWrapper<TestComposite,?>) new ComparableWrapper<>(differentFrom3, testClassThreeComparator);
        ComparableWrapper<TestComposite,?> wrap2 = (ComparableWrapper<TestComposite,?>) new ComparableWrapper<>(differentFrom2, testClassThreeComparator);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

    }
}