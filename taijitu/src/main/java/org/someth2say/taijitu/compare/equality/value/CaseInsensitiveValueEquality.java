package org.someth2say.taijitu.compare.equality.value;

import org.apache.commons.lang3.StringUtils;
import org.someth2say.taijitu.config.interfaces.DefaultEqualityConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

import java.util.Locale;

public class CaseInsensitiveValueEquality extends AbstractComparableValueEquality<String> {

    public static final String NAME = "caseInsensitive";
    private final Locale locale;

    public CaseInsensitiveValueEquality(Object equalityConfig) {
        super(equalityConfig);
        locale = equalityConfig != null && !StringUtils.isBlank(equalityConfig.toString()) ? Locale.forLanguageTag(equalityConfig.toString()) : Locale.getDefault();
    }

    @Override
    public int computeHashCode(String object) {
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

    public static IEqualityCfg defaultConfig() {
        return (DefaultEqualityConfig) () -> CaseInsensitiveValueEquality.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
