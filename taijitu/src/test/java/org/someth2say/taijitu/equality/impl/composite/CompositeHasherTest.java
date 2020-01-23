package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.equality.wrapper.Wrappers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassThreeHasher;

public class CompositeHasherTest {

    @Test
    public void testCompositeHasher() {
        TestComposite tc_aa1 = new TestComposite("aaa", "aaa", 1);
        TestComposite tc_bc1 = new TestComposite("bbb", "ccc", 1);
        TestComposite tc_aa2 = new TestComposite("aaa", "AAA", 2);

        assertTrue(testClassThreeHasher.hash(tc_aa1) == testClassThreeHasher.hash(tc_bc1));
        assertFalse(testClassThreeHasher.hash(tc_aa1) == testClassThreeHasher.hash(tc_aa2));
        
        var factory = Wrappers.factory(testClassThreeHasher);
        
        assertTrue(factory.wrap(tc_aa1).hashCode() == factory.wrap(tc_bc1).hashCode());
        assertFalse(factory.wrap(tc_aa1).hashCode() == factory.wrap(tc_aa2).hashCode());

    }
}