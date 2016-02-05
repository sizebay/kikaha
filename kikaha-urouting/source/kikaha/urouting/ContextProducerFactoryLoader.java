package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.ContextProducerFactory;
import trip.spi.Producer;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings("rawtypes")
public class ContextProducerFactoryLoader {

	@ProvidedServices(exposedAs=ContextProducer.class)
	Iterable<ContextProducer> availableProducers;
	
	ContextProducerFactory factory;

	@PostConstruct
	public void onStartup() {
		final Map<Class, ContextProducer> producers = loadAllProducers();
		factory = new ContextProducerFactory(producers);
	}
	
	@Producer
	public ContextProducerFactory produceFactory(){
		return factory;
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
