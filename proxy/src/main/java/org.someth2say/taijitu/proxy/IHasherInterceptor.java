package org.someth2say.taijitu.compare.equality.proxy;

import net.bytebuddy.implementation.bind.annotation.This;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

public interface IHasherInterceptor<T, EQ extends Hasher<T>> extends IEqualizableInterceptor<T,EQ> {

    default int hashCode(@This T t) {
        return getEquality().hash(t);
    }
}
