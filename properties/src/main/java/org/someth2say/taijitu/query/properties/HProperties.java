package org.someth2say.taijitu.query.properties;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Jordi Sola on 15/02/2017.
 */
public class HProperties implements Map<String, String>, Cloneable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4404465914050894580L;
	public static final String DEFAULT_HIERARCHY_SEPARATOR = ".";
    private final Properties delegate;
    private final String separator;

    public HProperties() {
        this(new Properties(), DEFAULT_HIERARCHY_SEPARATOR);
    }

    public HProperties(String separator) {
        this(new Properties(), separator);
    }

    public HProperties(Properties delegate) {
        this(delegate, DEFAULT_HIERARCHY_SEPARATOR);
    }

    public HProperties(Properties delegate, String separator) {
        this.delegate = delegate;
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public Properties getDelegate() {
        return delegate;
    }

    public HProperties getPropertiesByPrefix(final String... prefix) {
        return new HProperties(HPropertiesHelper.getPropertiesByPrefix(delegate, separator, prefix));
    }

    public HProperties getSubPropertiesByPrefix(final String... prefix) {
        return new HProperties(HPropertiesHelper.getSubPropertiesByPrefix(delegate, separator, prefix));
    }

    public HProperties getPropertiesByPrefix(final String prefix, final boolean removePrefix) {
        return new HProperties(HPropertiesHelper.getPropertiesByPrefix(delegate, prefix, removePrefix));
    }

    public Set<String> getPropertiesRoots() {
        return HPropertiesHelper.getPropertiesRoots(delegate, separator);
    }

    public String getHierarchycalProperty(final String property, final String root, final String... sections) {
        return HPropertiesHelper.getHierarchicalProperty(delegate, property, root, separator, sections);
    }

    public String getProperty(String property, String... sections) {
        return HPropertiesHelper.getProperty(delegate, property, separator, sections);
    }

    public String putInSections(String value, String... sections) {
        return put(HPropertiesHelper.joinSections(separator, sections), value);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsValue(Object o) {
        return delegate.containsValue(o);
    }

    @Override
    public boolean containsKey(Object o) {
        return delegate.containsKey(o);
    }

    @Override
    public String get(Object o) {
        final Object obj = delegate.get(o);
        return obj != null ? obj.toString() : null;
    }

    @Override
    public String put(String o, String o2) {
        final Object result = delegate.put(o, o2);
        return result != null ? result.toString() : null;
    }

    @Override
    public String remove(Object o) {
        final Object obj = delegate.remove(o);
        return obj != null ? obj.toString() : null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        delegate.putAll(map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        final Set<Object> objectSet = delegate.keySet();
        final Set<String> result = new HashSet<>();
        for (Object object : objectSet) {
            result.add(object.toString());
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        final Set<Entry<Object, Object>> objectSet = delegate.entrySet();
        final Set<Map.Entry<String, String>> result = new HashSet<>();
        for (Map.Entry<Object, Object> entry : objectSet) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            result.add(new HPropertiesEntry(key != null ? key.toString() : null, value != null ? value.toString() : null));
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Collection<String> values() {
        final Collection<Object> values = delegate.values();
        Collection<String> result = new ArrayList<>(values.size());
        for (Object value : values) {
            result.add(value != null ? value.toString() : null);
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public String joinSections(int start, int end, String... sections) {
        return HPropertiesHelper.joinSections(start, end, separator, sections);
    }

    public String joinSections(String... sections) {
        return HPropertiesHelper.joinSections(separator, sections);
    }

    @Override
    public HProperties clone() {
        return new HProperties((Properties) getDelegate().clone(), getSeparator());
    }

    private class HPropertiesEntry implements Entry<String, String> {
        private final String key;
        private String value;

        public HPropertiesEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            final String result = this.value;
            this.value = value;
            return result;
        }
    }
}
