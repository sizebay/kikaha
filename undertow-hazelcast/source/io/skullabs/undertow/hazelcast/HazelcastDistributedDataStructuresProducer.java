package io.skullabs.undertow.hazelcast;

import com.hazelcast.core.*;
import trip.spi.*;

@Service
public class HazelcastDistributedDataStructuresProducer {

	@Provided
	HazelcastInstance hazelcast;

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IMap map = hazelcast.getMap( name );
		return map;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IQueue produceIQueues( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IQueue<Object> queue = hazelcast.getQueue( name );
		return queue;
	}

	String retrieveMapNameFrom( final ProviderContext context ) {
		final Source name = context.getAnnotation( Source.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce a Map: no name provided" );
	}
}
