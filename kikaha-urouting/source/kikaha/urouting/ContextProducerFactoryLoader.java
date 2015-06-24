package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.ContextProducerFactory;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;
import trip.spi.StartupListener;

@SuppressWarnings("rawtypes")
@Singleton( exposedAs=StartupListener.class )
public class ContextProducerFactoryLoader implements StartupListener {

	@ProvidedServices(exposedAs=ContextProducer.class)
	Iterable<ContextProducer> availableProducers;

	@Override
	public void onStartup(final ServiceProvider provider) {
		final Map<Class, ContextProducer> producers = loadAllProducers();
		final ContextProducerFactory factory = new ContextProducerFactory(producers);
		provider.providerFor(ContextProducerFactory.class, factory);
	}

	private Map<Class, ContextProducer> loadAllProducers() {
		final Map<Class, ContextProducer> producers = new HashMap<>();
		for ( final ContextProducer producer : availableProducers ){
			final Class<?> forClazz = Reflection.getFirstGenericTypeFrom( producer, ContextProducer.class );
			producers.put( forClazz, producer );
		}
		return producers;
	}
}
