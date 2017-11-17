package org.someth2say.taijitu.compare.equality.value;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class StringCaseInsensitive<T extends String> extends AbstractConfigurableComparableCategorizerEquality<T> {

    private final Locale locale;

    public StringCaseInsensitive(){
        this(null);
    }

    public StringCaseInsensitive(Object equalityConfig) {
        super(equalityConfig);
        locale = equalityConfig != null && !StringUtils.isBlank(equalityConfig.toString()) ? Locale.forLanguageTag(equalityConfig.toString()) : Locale.getDefault();
    }

    @Override
    public int hashCode(T object) {
        return object.toUpperCase(locale).hashCode();
    }

    @Override
    public boolean equals(T object1, T object2) {
        return object1.toUpperCase().equals(object2.toUpperCase(locale));
    }

    @Override
    public int compare(T object1, T object2) {
        return object1.toUpperCase(locale).compareTo(object2.toUpperCase(locale));
    }

}
