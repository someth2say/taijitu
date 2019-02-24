package org.someth2say.taijitu.equality.impl.value;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

import java.util.Locale;

public class StringCaseInsensitiveComparatorHasher implements ComparatorHasher<String> {

    private final Locale locale;
    public static final StringCaseInsensitiveComparatorHasher INSTANCE = new StringCaseInsensitiveComparatorHasher();

    public StringCaseInsensitiveComparatorHasher(){
        this(Locale.getDefault());
    }

    public StringCaseInsensitiveComparatorHasher(Locale locale) {
        this.locale = locale;
    }

    @Override
    public int hash(String object) {
        return object.toUpperCase(getLocale()).hashCode();
    }

    @Override
    public boolean areEquals(String object1, String object2) {
        return object1.toUpperCase().equals(object2.toUpperCase(getLocale()));
    }

    @Override
    public int compare(String object1, String object2) {
        return object1.toUpperCase(getLocale()).compareTo(object2.toUpperCase(getLocale()));
    }

    public Locale getLocale() {
        return locale;
    }
}
