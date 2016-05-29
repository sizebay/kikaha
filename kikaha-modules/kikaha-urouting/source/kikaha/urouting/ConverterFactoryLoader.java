package kikaha.urouting;

import java.util.*;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.*;
import javax.inject.*;
import kikaha.urouting.api.*;

@Singleton
@SuppressWarnings("rawtypes")
public class ConverterFactoryLoader {

	@Inject
	@Typed( AbstractConverter.class )
	Iterable<AbstractConverter> availableConverters;
	
	ConverterFactory factory;

	@PostConstruct
	public void onStartup() {
		factory = new ConverterFactory( loadAllConverters() );
	}
	
	@Produces
	public ConverterFactory produceFactory(){
		return factory;
	}

	public Map<String, AbstractConverter<?>> loadAllConverters() {
		final Map<String, AbstractConverter<?>> converters = loadPrimitiveConverters();
		for ( final AbstractConverter converter : availableConverters ){
			final String canonicalName = converter.getGenericClass().getCanonicalName();
			converters.put(canonicalName, converter);
		}
		return converters;
	}

	static private Map<String, AbstractConverter<?>> loadPrimitiveConverters(){
		final Map<String, AbstractConverter<?>> primitiveConverters = new HashMap<>();
		converterFrom( primitiveConverters, Integer.TYPE, Integer::parseInt );
		converterFrom( primitiveConverters, Float.TYPE, Float::parseFloat );
		converterFrom( primitiveConverters, Double.TYPE, Double::parseDouble );
		converterFrom( primitiveConverters, Long.TYPE, Long::parseLong );
		converterFrom( primitiveConverters, Short.TYPE, Short::parseShort );
		converterFrom( primitiveConverters, Boolean.TYPE, Boolean::parseBoolean );
		return primitiveConverters;
	}
	
	static private <T> void converterFrom(
			Map<String, AbstractConverter<?>> primitiveConverters,
			Class<T> primitiveType, Function<String, T> converter)
	{
		primitiveConverters.put(
			primitiveType.getCanonicalName(),
			new AbstractConverter<T>() {
				@Override
				public T convert(String value) throws ConversionException { return converter.apply(value); }
				@Override
				public Class<T> getGenericClass() { return primitiveType; }
			}
		);
	}
}
