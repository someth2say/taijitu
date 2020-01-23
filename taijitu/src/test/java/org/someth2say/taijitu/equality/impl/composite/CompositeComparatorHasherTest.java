package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.ComparableHashableWrapper;
import org.someth2say.taijitu.equality.wrapper.Wrappers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassThreeComparatorHasher;

public class CompositeComparatorHasherTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeComparatorHasher.compare(differentFrom1, differentFrom3) < 0);
        assertTrue(testClassThreeComparatorHasher.compare(differentFrom1, differentFrom2) == 0);
        assertTrue(testClassThreeComparatorHasher.areEquals(differentFrom1, differentFrom2));
        
        var wrapper = Wrappers.factory(testClassThreeComparatorHasher);
        ComparableHashableWrapper<TestComposite> wrap1 = wrapper.wrap(differentFrom1);
        ComparableHashableWrapper<TestComposite> wrap3 = wrapper.wrap(differentFrom3);
        ComparableHashableWrapper<TestComposite> wrap2 = wrapper.wrap(differentFrom2);

        assertTrue(wrap1.compareTo(wrap3) < 0);
        assertTrue(wrap1.compareTo(wrap2) == 0);
        assertTrue(wrap1.equals(wrap2));

        assertTrue(testClassThreeComparatorHasher.hash(differentFrom1) == testClassThreeComparatorHasher.hash(differentFrom2));
        assertFalse(testClassThreeComparatorHasher.hash(differentFrom1) == testClassThreeComparatorHasher.hash(differentFrom3));
        assertTrue(new ComparableHashableWrapper<>(differentFrom1, testClassThreeComparatorHasher).hashCode() == new ComparableHashableWrapper<>(differentFrom2, testClassThreeComparatorHasher).hashCode());
        assertFalse(new ComparableHashableWrapper<>(differentFrom1, testClassThreeComparatorHasher).hashCode() == new ComparableHashableWrapper<>(differentFrom3, testClassThreeComparatorHasher).hashCode());

    }
}