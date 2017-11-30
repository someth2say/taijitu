package org.someth2say.taijitu;

import org.someth2say.taijitu.compare.equality.composite.CompositeComparableEquality;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.value.ObjectToString;
import org.someth2say.taijitu.compare.equality.value.StringCaseInsensitive;

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


    public static CompositeEquality<TestComposite> testClassOneTwoEquality = new CompositeEquality.Builder<TestComposite>()
            .addComponent(TestComposite::getOne, new StringCaseInsensitive())
            .addComponent(TestComposite::getTwo, new StringCaseInsensitive()).build();

    public static CompositeComparableEquality<TestComposite> testClassThreeComparer = new CompositeComparableEquality.Builder<TestComposite>()
            .addComponent(TestComposite::getThree, new ObjectToString<>()).build();
}
