package kikaha.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import kikaha.core.cdi.ProviderContext;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@SuppressWarnings( "rawtypes" )
public class HazelcastDistributedDataStructuresProducer {

	@Inject
	HazelcastInstance hazelcast;

	@Produces
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IMap map = hazelcast.getMap( name );
		return map;
	}

	@Produces
	public IdGenerator produceIdMaps(final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IdGenerator map = hazelcast.getIdGenerator( name );
		return map;
	}

	String retrieveSourceNameFrom( final ProviderContext context ) {
		final Named name = context.getAnnotation( Named.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce data: no name provided" );
	}
}
