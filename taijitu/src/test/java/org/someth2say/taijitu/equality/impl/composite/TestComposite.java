package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.impl.value.*;

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


    public static final CompositeComparatorHasher<TestComposite> testClassOneTwoEquality
            = new CompositeComparatorHasher.Builder<TestComposite>()
            .addComparatorHasher(TestComposite::getThree, NumberComparatorHasher.INSTANCE)
            .addComparatorHasher(TestComposite::getOne, StringCaseInsensitiveComparatorHasher.INSTANCE)
            .addComparatorHasher(TestComposite::getTwo, StringCaseInsensitiveComparatorHasher.INSTANCE)
            .build();

    @SuppressWarnings({"unchecked"})
    public static final CompositeComparator<TestComposite> testClassThreeComparator
            = new CompositeComparator.Builder<TestComposite>()
            .addComparator(TestComposite::getThree, (ComparableComparatorHasher<Integer>)ComparableComparatorHasher.INSTANCE)
            .build();

    public static final CompositeHasher<TestComposite> testClassThreeHasher
            = new CompositeHasher.Builder<TestComposite>()
            .addHasher(TestComposite::getThree, ObjectHasher.INSTANCE)
            .build();

    public static final CompositeComparatorHasher<TestComposite> testClassThreeComparatorHasher
            = new CompositeComparatorHasher.Builder<TestComposite>()
            .addComparatorHasher(TestComposite::getThree, ObjectToStringComparatorHasher.INSTANCE)
            .build();
}
