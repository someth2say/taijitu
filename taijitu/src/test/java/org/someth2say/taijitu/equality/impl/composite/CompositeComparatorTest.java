package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.ComparableWrapper;
import org.someth2say.taijitu.equality.wrapper.Wrappers;

import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassThreeComparator;

public class CompositeComparatorTest {

    @Test
    public void testCompositeEquality() {
        TestComposite tc_aa1 = new TestComposite("aaa", "aaa", 1);
        TestComposite tc_bc1 = new TestComposite("bbb", "ccc", 1);
        TestComposite tc_aA2 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparator.compare(tc_aa1, tc_aA2) < 0);
        assertTrue(testClassThreeComparator.compare(tc_aa1, tc_bc1) == 0);
        assertTrue(testClassThreeComparator.areEquals(tc_aa1, tc_bc1));

        var factory = Wrappers.factory(testClassThreeComparator);
        ComparableWrapper<TestComposite> wrap1 = factory.wrap(tc_aa1); // new ComparableWrapper<>(tc_aa1, testClassThreeComparator);
        ComparableWrapper<TestComposite> wrap3 = factory.wrap(tc_aA2); // new ComparableWrapper<>(tc_aA2, testClassThreeComparator);
        ComparableWrapper<TestComposite> wrap2 = factory.wrap(tc_bc1); // new ComparableWrapper<>(tc_bc1, testClassThreeComparator);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

    }
}