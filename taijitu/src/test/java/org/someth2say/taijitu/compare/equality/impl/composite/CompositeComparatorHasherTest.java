package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableWraper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassThreeComparatorHasher;

public class CompositeComparatorHasherTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparatorHasher.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparatorHasher.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparatorHasher.equals(differentFrom1, differentFrom2));

        IComparableWraper<TestComposite, ?> wrap1 = testClassThreeComparatorHasher.wrap(differentFrom1);
        IComparableWraper<TestComposite, ?> wrap3 = testClassThreeComparatorHasher.wrap(differentFrom3);
        IComparableWraper<TestComposite, ?> wrap2 = testClassThreeComparatorHasher.wrap(differentFrom2);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

        assertTrue(testClassThreeComparatorHasher.hashCode(differentFrom1)== testClassThreeComparatorHasher.hashCode(differentFrom2));
        assertFalse(testClassThreeComparatorHasher.hashCode(differentFrom1)==testClassThreeComparatorHasher.hashCode(differentFrom3));
        assertTrue(testClassThreeComparatorHasher.wrap(differentFrom1).hashCode() == testClassThreeComparatorHasher.wrap(differentFrom2).hashCode());
        assertFalse(testClassThreeComparatorHasher.wrap(differentFrom1).hashCode()==testClassThreeComparatorHasher.wrap(differentFrom3).hashCode());

    }
}