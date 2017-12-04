package org.someth2say.taijitu.compare.equality.aspects.internal;


import org.someth2say.taijitu.compare.equality.aspects.external.CategorizerEquality;

public interface CategorizableEqualizable<T, EQ extends CategorizerEquality<T>> extends Equalizable<T, EQ>, Categorizable<T, EQ> {
}
