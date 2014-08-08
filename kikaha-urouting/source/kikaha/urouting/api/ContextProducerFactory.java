package kikaha.urouting.api;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.Reflection;
import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings( { "rawtypes", "unchecked" } )
public class ContextProducerFactory {

	@Provided
	ServiceProvider provider;
	Map<Class, ContextProducer> producers;

	public <T> ContextProducer<T> producerFor( Class<T> clazz ) throws RoutingException {
		return getProducers().get( clazz );
	}

	public Map<Class, ContextProducer> getProducers() throws RoutingException {
		if ( this.producers == null ) {
			producers = new HashMap<Class, ContextProducer>();
			loadProducers();
		}
		return this.producers;
	}

	private void loadProducers() throws RoutingException {
		try {
			val producers = provider.loadAll( ContextProducer.class );
			for ( ContextProducer producer : producers )
				register( producer );
		} catch ( ServiceProviderException cause ) {
			throw new RoutingException( cause );
		}
	}

	public <T> void register( ContextProducer<T> producer ) {
		val forClazz = Reflection.getFirstGenericTypeFrom( producer, ContextProducer.class );
		producers.put( forClazz, producer );
	}
}