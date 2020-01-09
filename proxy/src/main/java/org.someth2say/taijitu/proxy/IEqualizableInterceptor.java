package org.someth2say.taijitu.compare.equality.proxy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.This;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public interface IEqualizableInterceptor<T, EQ extends Equalizer<T>> {

    default boolean equalsTo(@This T t1, @Argument(0) T t2) {
        return getEquality().areEquals(t1, t2);
    }

    default boolean equals(@This T t1, @Argument(0) Object t2) {
        return t2 != null&& getEquality().areEquals(t1, (T) t2);
    }

    EQ getEquality();
}
