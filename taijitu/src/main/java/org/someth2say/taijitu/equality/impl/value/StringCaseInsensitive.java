package org.someth2say.taijitu.equality.impl.value;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;

import java.util.Locale;

public class StringCaseInsensitive extends AbstractConfigurableEqualizer<String> implements ComparatorHasher<String> {

    private final Locale locale;
    public static final StringCaseInsensitive EQUALITY = new StringCaseInsensitive();

    public StringCaseInsensitive(){
        this(null);
    }

    public StringCaseInsensitive(Object equalityConfig) {
        super(equalityConfig);
        locale = equalityConfig != null && !StringUtils.isBlank(equalityConfig.toString()) ? Locale.forLanguageTag(equalityConfig.toString()) : Locale.getDefault();
    }

    @Override
    public int hash(String object) {
        return object.toUpperCase(locale).hashCode();
    }

    @Override
    public boolean areEquals(String object1, String object2) {
        return object1.toUpperCase().equals(object2.toUpperCase(locale));
    }

    @Override
    public int compare(String object1, String object2) {
        return object1.toUpperCase(locale).compareTo(object2.toUpperCase(locale));
    }

}
