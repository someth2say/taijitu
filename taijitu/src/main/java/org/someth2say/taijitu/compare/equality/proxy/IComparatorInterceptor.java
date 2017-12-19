package org.someth2say.taijitu.compare.equality.proxy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.This;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;

public interface IComparatorInterceptor<T, EQ extends Comparator<T>> extends IEqualizableInterceptor<T,EQ> {

    default int compareTo(@This T t1, @Argument(0) T t2) {
        return getEquality().compare(t1, t2);
    }
}
