package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConverterFactory;
import trip.spi.Producer;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings("rawtypes")
public class ConverterFactoryLoader {

	@ProvidedServices( exposedAs=AbstractConverter.class )
	Iterable<AbstractConverter> availableConverters;
	
	ConverterFactory factory;

	@PostConstruct
	public void onStartup() {
		factory = new ConverterFactory( loadAllConverters() );
	}
	
	@Producer
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
