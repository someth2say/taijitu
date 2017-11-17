package org.someth2say.taijitu.compare.equality.composite;

import java.util.Collection;
import java.util.function.Function;

/**
 * 
 * @author Jordi Sola
 *	A composite is something that can be decomposed, by using a set of extractor functions.
 *  In other words, a composite is a set of extractors.
 *  Unluckily 'Function' type does not define equality, so we can't use `Set`
 *
 * @param <T> The self type for composite
 */

public interface Composite<T> {	
	Collection<Function<T,?>> getExtractors();
}
