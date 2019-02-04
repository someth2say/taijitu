package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.compare.equality.impl.composite.TestComposite.testClassOneTwoEquality;

public class CompositeEqualizerTest {
    @Test
    public void testCompositeEquality() {
        TestComposite differentFrom1 = new TestComposite("aaa", "aaa", 1);
        TestComposite differentFrom2 = new TestComposite("bbb", "ccc", 1);
        TestComposite differentFrom3 = new TestComposite("aaa", "AAA", 2);

        assertFalse(testClassOneTwoEquality.areEquals(differentFrom1, differentFrom2));
        assertTrue(testClassOneTwoEquality.areEquals(differentFrom1, differentFrom3));

    }
}
