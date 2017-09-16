package org.someth2say.taijitu.query.properties;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.commons.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * @author Jordi Sola
 */
public final class HPropertiesHelper {
    public static final String INCLUDE_KEYWORD = "include.file";

    private HPropertiesHelper() {
    }

    public static Properties getPropertiesByPrefix(final Properties properties, String separator, final String... prefix) {
        return getPropertiesByPrefix(properties, joinSections(separator, prefix), false);
    }

    public static Properties getSubPropertiesByPrefix(final Properties properties, String separator, final String... prefix) {
        return getPropertiesByPrefix(properties, joinSections(separator, prefix) + separator, true);
    }

    /**
     * Retrieves all entries in provided properties whose keys starts with 'prefix'
     * Note prefix need not need to be a complete set of sections, nor need to end with a hierarchy separator.
     * If `removePrefix` is true, the prefix will be stripped out from returning properties' keys.
     *
     * @param properties
     * @param prefix
     * @param removePrefix
     * @return
     */
    public static Properties getPropertiesByPrefix(final Properties properties, final String prefix, final boolean removePrefix) {
        final Set<Entry<Object, Object>> entries = properties.entrySet();
        final Properties result = new Properties();
        for (final Entry<Object, Object> entry : entries) {
            final String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                if (removePrefix) {
                    final String newKey = entry.getKey().toString().substring(prefix.length());
                    result.put(newKey, entry.getValue());
                } else {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * @param properties
     * @param separator
     */
    public static Set<String> getPropertiesRoots(final Properties properties, String separator) {
        final Set<String> result = new HashSet<>();
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            final String key = entry.getKey().toString();
            final String[] split = StringUtils.split(key, separator);
            result.add(split[0]);
        }
        return result;
    }

    /**
     * Tries to find a property located on a section, or on any of its parents.
     * That is:
     * <ol>
     * <li>Looks for root.section1.section2.section3.paramName</li>
     * <li>If not found, looks for root.section1.section2.paramName</li>
     * <li>If not found, looks for root.section1.paramName</li>
     * <li>If not found, looks for root.paramName</li>
     * </ol>
     * If not found on any case, returns null.
     * Please, note that both <code>root</code> and <code>paramName</code> can contain section separators ("."). Also can <code>sections</code>, but is discouraged.
     *
     * @param properties The properties to be used for finding the resulting value
     * @param paramName  Ending part for the property. May be a composed value, but never null.
     * @param root       Initial part for the property. May be a composed value (i.e. A.B), or null.
     * @param separator
     * @param sections   Middle part for the property, that may be used (or not) for finding the right value.
     */

    public static String getHierarchicalProperty(Properties properties, final String paramName, final String root, String separator, final String... sections) {
        int sectionsLenght = sections.length;

        while (sectionsLenght > 0) {
            final String joinedSections = joinSections(0, sectionsLenght, separator, sections);
            String currentKey = root != null ? joinSections(separator, root, joinedSections, paramName) : joinSections(separator, joinedSections, paramName);
            final String currentValue = properties.getProperty(currentKey);
            if (currentValue != null) {
                return currentValue;
            }
            sectionsLenght--;
        }
        // Here, sectionsLength should be zero! So last try will be 'root.paramName'
        String currentKey = root != null ? joinSections(separator, root, paramName) : paramName;
        return properties.getProperty(currentKey);
    }


    public static HProperties load(final File file) throws HPropertiesException {
        return load(file, INCLUDE_KEYWORD);
    }

    public static HProperties load(final File file, String includeKeyword) throws HPropertiesException {
        try (InputStream fos = new FileInputStream(file)) {
            return loadAndResolveIncludes(new HashSet<>(), file, fos, includeKeyword);
        } catch (IOException e) {
            throw new HPropertiesException("Unable to load properties file " + file.getAbsolutePath(), e);
        }
    }

    public static HProperties load(InputStream fos) throws IOException, HPropertiesException {
        return load(fos, INCLUDE_KEYWORD);
    }

    public static HProperties load(InputStream fos, String includeKeyword) throws IOException, HPropertiesException {
        Properties result = new Properties();
        result.load(fos);
        resolveIncludes(result, new HashSet<>(), includeKeyword);
        return new HProperties(result);
    }

    private static void resolveIncludes(Properties properties, Set<File> alreadyLoaded, String includeKeyword) throws HPropertiesException, IOException {
        final Properties includeProperties = getPropertiesByPrefix(properties, includeKeyword, false);
        for (Entry<Object, Object> includeEntry : includeProperties.entrySet()) {
            final File file = new File(includeEntry.getValue().toString());
            if (!alreadyLoaded.contains(file)) {
                try (InputStream fos = new FileInputStream(file)) {
                    HProperties importProperties = loadAndResolveIncludes(alreadyLoaded, file, fos, includeKeyword);
                    properties.putAll(importProperties);
                }
            }
        }
    }

    private static HProperties loadAndResolveIncludes(Set<File> alreadyLoaded, File file, InputStream fos, String includeKeyword) throws IOException, HPropertiesException {
        Properties result = new Properties();
        result.load(fos);
        alreadyLoaded.add(file);
        resolveIncludes(result, alreadyLoaded, includeKeyword);
        return new HProperties(result);
    }

    public static String joinSections(String separator, String... sections) {
        return StringUtil.join(sections, separator);
    }

    public static String joinSections(int start, int end, String separator, String... sections) {
        return StringUtil.join(sections, separator, start, end);
    }

    public static String getProperty(Properties config, String property, String separator, String... sections) {
        if (sections.length == 0) {
            return config.getProperty(property);
        }
        final String section = joinSections(separator, sections);
        return config.getProperty(joinSections(separator, section, property));
    }
}
