package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.source.mapper.CSVTupleMapper;
import org.someth2say.taijitu.source.mapper.ResultSetTupleMapper;
import org.someth2say.taijitu.source.mapper.SourceMapper;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class MapperRegistry {
	private static final Logger logger = Logger.getLogger(MapperRegistry.class);
	private static Map<String, Class<? extends SourceMapper>> classes = new ConcurrentHashMap<>();

	private MapperRegistry() {
	}

	private static Class<? extends SourceMapper> getValueEqualityType(final String name) {
		return classes.get(name);
	}

	public static void scanClassPath() {
		// This seems fast enough for a one-shot initialization
		// If found slow, it can be changed to scan only sub-packages
		final Class<SourceMapper> implementedInterface = SourceMapper.class;
		classes = ClassScanUtils.getClassesImplementing(implementedInterface);

		logger.info("Registered value equalities: " + classes.keySet().toString());
	}

	public static void useDefaults() {
		addMapper(CSVTupleMapper.NAME, CSVTupleMapper.class);
		addMapper(ResultSetTupleMapper.NAME, ResultSetTupleMapper.class);
	}

	private static void addMapper(String name, Class<? extends SourceMapper<?, ?>> clazz) {
		classes.put(name, clazz);
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
