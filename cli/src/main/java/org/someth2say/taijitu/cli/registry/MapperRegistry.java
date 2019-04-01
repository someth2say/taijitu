package org.someth2say.taijitu.cli.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.source.mapper.SourceMapper;
import org.someth2say.taijitu.cli.source.mapper.CSVTupleMapper;
import org.someth2say.taijitu.cli.source.mapper.ResultSetTupleMapper;
import org.someth2say.taijitu.cli.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class MapperRegistry {
	private static final Logger logger = LoggerFactory.getLogger(MapperRegistry.class);
	private static Map<String, Class<? extends SourceMapper>> classes = new ConcurrentHashMap<>();

	private MapperRegistry() {
	}

	private static Class<? extends SourceMapper> getValueEqualityType(final String name) {
		return classes.get(name);
	}

	public static void scanClassPath() {
		// This seems fast enough for a one-shot initialization
		// If found slow, it can be changed to scan only sub-packages
		classes = ClassScanUtils.getNamedClassesImplementing(SourceMapper.class);
		logger.info("Registered mappers: {}", classes.keySet().toString());
	}

	public static void useDefaults() {
		addMapper(CSVTupleMapper.class);
		addMapper(ResultSetTupleMapper.class);
	}

	private static void addMapper(Class<? extends SourceMapper<?, ?>> clazz) {
		classes.put(ClassScanUtils.getClassName(clazz), clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <S,T> SourceMapper<S, T> getInstance(String type) {
		Class<? extends SourceMapper> equalityType = getValueEqualityType(type);
		try {
			return equalityType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			logger.error("Unable to create mapper. Type: " + type, e);
		}
		return null;
	}
}
