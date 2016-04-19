package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConverterFactory;

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
		final Map<String, AbstractConverter<?>> converters = new HashMap<>();
		for ( final AbstractConverter converter : availableConverters ){
			final String canonicalName = converter.getGenericClass().getCanonicalName();
			converters.put(canonicalName, converter);
		}
		return converters;
	}
}
