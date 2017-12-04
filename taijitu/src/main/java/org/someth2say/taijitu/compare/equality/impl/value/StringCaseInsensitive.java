package org.someth2say.taijitu.compare.equality.impl.value;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorCategorizerEquality;

import java.util.Locale;

public class StringCaseInsensitive extends AbstractConfigurableEquality<String> implements ComparatorCategorizerEquality<String> {

    private final Locale locale;

    public StringCaseInsensitive(){
        this(null);
    }

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
