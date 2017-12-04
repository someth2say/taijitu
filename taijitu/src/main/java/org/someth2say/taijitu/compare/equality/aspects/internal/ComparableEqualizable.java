package org.someth2say.taijitu.compare.equality.aspects.internal;


import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorEquality;

public interface ComparableEqualizable<T,EQ extends ComparatorEquality<T>> extends Equalizable<T,EQ>, Comparable<T,EQ>{
}
