package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConverterFactory;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;
import trip.spi.StartupListener;

@SuppressWarnings("rawtypes")
@Singleton( exposedAs=StartupListener.class )
public class ConverterFactoryLoader implements StartupListener {

	@ProvidedServices( exposedAs=AbstractConverter.class )
	Iterable<AbstractConverter> availableConverters;

	@Override
	public void onStartup(final ServiceProvider provider) {
		final ConverterFactory factory = new ConverterFactory( loadAllConverters() );
		provider.providerFor(ConverterFactory.class, factory);
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
