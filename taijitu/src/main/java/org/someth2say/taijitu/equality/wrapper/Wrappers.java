package org.someth2say.taijitu.equality.wrapper;


import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.aspects.external.Hasher;

public class Wrappers {

    public static <WRAPPED> EqualizableWrapper<WRAPPED> wrap(WRAPPED wrapped, Equalizer<WRAPPED> equalizer) {
        return new EqualizableWrapper<>(wrapped, equalizer);
    }

    public static <WRAPPED> HashableWrapper<WRAPPED> wrap(WRAPPED wrapped, Hasher<WRAPPED> hasher) {
        return new HashableWrapper<>(wrapped, hasher);
    }

    public static <WRAPPED> ComparableWrapper<WRAPPED> wrap(WRAPPED wrapped, Comparator<WRAPPED> comparator) {
        return new ComparableWrapper<>(wrapped, comparator);
    }

    public static <WRAPPED> ComparableHashableWrapper<WRAPPED> wrap(WRAPPED wrapped, ComparatorHasher<WRAPPED> comparatorHasher) {
        return new ComparableHashableWrapper<>(wrapped, comparatorHasher);
    }

    public static <WRAPPED> EqualizableWrapper.Factory<WRAPPED> factory(Equalizer<WRAPPED> equalizer) {
        return new EqualizableWrapper.Factory<>(equalizer);
    }

    public static <WRAPPED> HashableWrapper.Factory<WRAPPED> factory(Hasher<WRAPPED> hasher) {
        return new HashableWrapper.Factory<>(hasher);
    }

    public static <WRAPPED> ComparableWrapper.Factory<WRAPPED> factory(Comparator<WRAPPED> comparator) {
        return new ComparableWrapper.Factory<>(comparator);
    }

    public static <WRAPPED> ComparableHashableWrapper.Factory<WRAPPED> factory(ComparatorHasher<WRAPPED> comparatorHasher) {
        return new ComparableHashableWrapper.Factory<>(comparatorHasher);
    }

}

