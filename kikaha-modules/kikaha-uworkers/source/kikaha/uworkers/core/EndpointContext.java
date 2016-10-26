package kikaha.uworkers.core;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import kikaha.config.*;

/**
 *
 */
@Singleton
public class EndpointContext {

	@Inject
	@Typed( EndpointFactory.class )
	Collection<EndpointFactory> factories;

	@Inject Config config;

	@PostConstruct
	public void sortFactories(){
		final List<EndpointFactory> factories = new ArrayList<>();
		factories.addAll( this.factories );
		Collections.sort( factories );
		this.factories = factories;
	}

	public int getEndpointParallelism( final String alias, final int defaultParallelism ){
		return config.getInteger("server.uworkers." + alias + ".parallelism", defaultParallelism );
	}

	public Config getEndpointConfig( final String alias ) {
		Config config = this.config.getConfig("server.uworkers." + alias);
		if ( config == null )
			config = MergeableConfig.create();
		return config;
	}

	public EndpointFactory getFactoryFor( String endpointName ) {
		for ( EndpointFactory factory : factories )
			if ( factory.canHandleEndpoint( endpointName ) )
				return factory;
		throw new IllegalArgumentException( "No factory available for " + endpointName );
	}
}
