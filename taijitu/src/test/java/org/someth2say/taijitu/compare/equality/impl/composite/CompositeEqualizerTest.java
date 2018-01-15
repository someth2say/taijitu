package org.someth2say.taijitu.compare.equality.impl.composite;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;
import org.someth2say.taijitu.compare.equality.wrapper.EqualizableWrapper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Difference<?>> differences = testClassOneTwoEquality.underlyingDiffs(differentFrom1, differentFrom2).collect(Collectors.toList());
        assertTrue(differences.containsAll(Arrays.asList(
                        new Unequal<>(StringCaseInsensitive.EQUALITY, "aaa", "bbb"),
                        new Unequal<>(StringCaseInsensitive.EQUALITY, "aaa", "ccc"))));

        assertFalse(new EqualizableWrapper<>(differentFrom1, testClassOneTwoEquality).equals(new EqualizableWrapper<>(differentFrom2, testClassOneTwoEquality)));
        assertTrue(new EqualizableWrapper<>(differentFrom1, testClassOneTwoEquality).equals(new EqualizableWrapper<>(differentFrom3, testClassOneTwoEquality)));

    }
}
