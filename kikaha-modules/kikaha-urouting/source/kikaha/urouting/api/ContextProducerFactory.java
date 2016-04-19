package kikaha.urouting.api;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@SuppressWarnings( { "rawtypes", "unchecked" } )
@RequiredArgsConstructor
public class ContextProducerFactory {

	final Map<Class, ContextProducer> producers;

	public <T> ContextProducer<T> producerFor( final Class<T> clazz ) throws RoutingException {
		return producers.get( clazz );
	}

}