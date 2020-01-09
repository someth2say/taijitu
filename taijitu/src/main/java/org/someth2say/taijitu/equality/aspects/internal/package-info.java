/**
 * This package contains the needed aspects to implement internal equality.
 * Unlike {@link org.someth2say.taijitu.equality.aspects.external}, internal equality places the responsibility of defining equality into the tested types/instances.
 * Despite internal equality is sometimes useful (for example, defining the default equality for a type), using only internal equality restricts the usage of the equality
 * framework with different equality definitions.
 */
package org.someth2say.taijitu.equality.aspects.internal;