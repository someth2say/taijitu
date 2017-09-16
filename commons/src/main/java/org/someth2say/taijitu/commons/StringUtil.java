package org.someth2say.taijitu.commons;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jordi Sola
 */
public final class StringUtil {

    final static String DEFAULT_SPLITTER = ",";

    private StringUtil() {
    }

    public static String join(final String[] parts, final String splitter) {
        return StringUtils.join(parts, splitter);
    }

    public static String join(final String[] parts, final String splitter, int start, int end) {
        return StringUtils.join(parts, splitter, start, end);
    }

    public static String join(final String[] parts) {
        return StringUtils.join(parts, DEFAULT_SPLITTER);
    }

    public static String[] splitAndTrim(final String string) {
        return splitAndTrim(string, DEFAULT_SPLITTER);
    }

    public static String[] splitAndTrim(String string, String splitter) {
        if (string == null) {
            return null;
        }

        final String[] splitted = StringUtils.split(string, splitter);
        for (int cnIdx = 0; cnIdx < splitted.length; cnIdx++) {
            splitted[cnIdx] = splitted[cnIdx].trim();
        }
        return splitted;
    }

    public static int[] findIndexes(String[] base, String[] find) {
        int[] result = new int[find.length];
        final List<String> bases = Arrays.asList(base);
        for (int findIdx = 0, findLength = find.length; findIdx < findLength; findIdx++) {
            result[findIdx] = bases.indexOf(find[findIdx]);
        }
        return result;
    }

    public static boolean[] getBitMap(String[] base, String[] find) {
        final List<String> findList = Arrays.asList(find);
        boolean[] result = new boolean[base.length];
        for (int idx = 0; idx < base.length; idx++) {
            result[idx] = findList.contains(base[idx]);
        }
        return result;
    }

    public static String[] retainAll(final String[] base, final String[] toRetain) {
        Collection<String> result = Arrays.asList(base);
        result.retainAll(Arrays.asList(toRetain));
        return result.toArray(new String[result.size()]);
    }
}
