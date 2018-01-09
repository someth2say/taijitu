package org.someth2say.taijitu.compare.equality.aspects.external;

public interface Hasher<HASHED> extends Equalizer<HASHED> {

    int hash(HASHED hashed);

}
