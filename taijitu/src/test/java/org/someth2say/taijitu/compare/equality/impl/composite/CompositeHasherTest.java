package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.TestComposite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.TestComposite.testClassThreeHasher;

public class CompositeHasherTest {

    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeHasher.hashCode(differentFrom1)== testClassThreeHasher.hashCode(differentFrom2));
        assertFalse(testClassThreeHasher.hashCode(differentFrom1)==testClassThreeHasher.hashCode(differentFrom3));
        assertTrue(testClassThreeHasher.wrap(differentFrom1).hashCode() == testClassThreeHasher.wrap(differentFrom2).hashCode());
        assertFalse(testClassThreeHasher.wrap(differentFrom1).hashCode()==testClassThreeHasher.wrap(differentFrom3).hashCode());


    }
}