package org.someth2say.taijitu;

import org.someth2say.taijitu.compare.equality.impl.composite.CompositeComparatorEqualizer;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeEqualizer;
import org.someth2say.taijitu.compare.equality.impl.composite.CompositeHasherEqualizer;
import org.someth2say.taijitu.compare.equality.impl.value.ObjectToString;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;

import java.util.Objects;

public class TestComposite {
    private final String one;
    private final String two;
    private final Integer three;

    public TestComposite(String one, String two, Integer three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public String getOne() {
        return one;
    }

    public String getTwo() {
        return two;
    }

    public Integer getThree() {
        return three;
    }

    @Override
    public String toString() {
        return "TestComposite{" +
                "one='" + one + '\'' +
                ", two='" + two + '\'' +
                ", three=" + three +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestComposite)) return false;
        TestComposite testComposite = (TestComposite) o;
        return Objects.equals(getOne(), testComposite.getOne()) &&
                Objects.equals(getTwo(), testComposite.getTwo()) &&
                Objects.equals(getThree(), testComposite.getThree());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOne(), getTwo(), getThree());
    }


    public static final CompositeEqualizer<TestComposite> testClassOneTwoEquality = new CompositeEqualizer.Builder<TestComposite>()
            .addComponent(TestComposite::getOne, new StringCaseInsensitive())
            .addComponent(TestComposite::getTwo, new StringCaseInsensitive()).build();

    public static final CompositeComparatorEqualizer<TestComposite> testClassThreeComparer = new CompositeComparatorEqualizer.Builder<TestComposite>()
            .addComponent(TestComposite::getThree, new ObjectToString<>()).build();

    public static final CompositeHasherEqualizer<TestComposite> testClassThreeHasher = new CompositeHasherEqualizer.Builder<TestComposite>()
            .addComponent(TestComposite::getThree, new ObjectToString<>()).build();
}
