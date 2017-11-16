package org.someth2say.taijitu.compare.equality.value;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class StringCaseInsensitive extends AbstractConfigurableComparableCategorizerEquality<String> {

    private final Locale locale;

    public StringCaseInsensitive(Object equalityConfig) {
        super(equalityConfig);
        locale = equalityConfig != null && !StringUtils.isBlank(equalityConfig.toString()) ? Locale.forLanguageTag(equalityConfig.toString()) : Locale.getDefault();
    }

    @Override
    public int hashCode(String object) {
        return object.toUpperCase(locale).hashCode();
    }

    @Override
    public boolean equals(String object1, String object2) {
        return object1.toUpperCase().equals(object2.toUpperCase(locale));
    }

    @Override
    public int compare(String object1, String object2) {
        return object1.toUpperCase(locale).compareTo(object2.toUpperCase(locale));
    }

}
