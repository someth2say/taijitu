package org.someth2say.taijitu.compare.equality;

import java.util.Collection;

import org.someth2say.taijitu.compare.equality.composite.Composite;
import org.someth2say.taijitu.compare.result.Mismatch;

public interface Matcher<T extends Composite<T>> extends Equality<T> {
	
	public Collection<Mismatch<T>> match(T t1, T t2);

}
