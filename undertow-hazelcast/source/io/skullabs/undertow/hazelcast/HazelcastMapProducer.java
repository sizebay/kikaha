package io.skullabs.undertow.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import trip.spi.*;

@Service
public class HazelcastMapProducer {

	@Provided
	HazelcastInstance hazelcast;

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IMap map = hazelcast.getMap( name );
		return map;
	}

	String retrieveMapNameFrom( final ProviderContext context ) {
		final Source name = context.getAnnotation( Source.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce a Map: no name provided" );
	}
}
