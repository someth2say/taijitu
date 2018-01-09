package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.HashableWrapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassThreeHasher;

public class CompositeHasherTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeHasher.hash(differentFrom1)== testClassThreeHasher.hash(differentFrom2));
        assertFalse(testClassThreeHasher.hash(differentFrom1)==testClassThreeHasher.hash(differentFrom3));
        assertTrue(new HashableWrapper<>(differentFrom1, testClassThreeHasher).hashCode() == new HashableWrapper<>(differentFrom2, testClassThreeHasher).hashCode());
        assertFalse(new HashableWrapper<>(differentFrom1, testClassThreeHasher).hashCode()== new HashableWrapper<>(differentFrom3, testClassThreeHasher).hashCode());


    }
}