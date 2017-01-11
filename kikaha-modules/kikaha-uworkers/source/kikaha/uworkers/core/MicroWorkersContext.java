package kikaha.uworkers.core;

import kikaha.config.Config;
import kikaha.config.MergeableConfig;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Singleton
public class MicroWorkersContext {

	@Inject
	@Typed( EndpointFactory.class )
	Collection<EndpointFactory> factories;

	@Inject Config config;
	@Getter boolean isRestEnabled;
	@Getter String restApiPrefix;
	@Getter int maxTaskPoolSize;

	@PostConstruct
	public void configureEndpointContext(){
		sortFactories();
		isRestEnabled = config.getBoolean( "server.uworkers.rest-api.enabled" );
		restApiPrefix = config.getString( "server.uworkers.rest-api.base-endpoint" );
		maxTaskPoolSize = config.getInteger( "server.uworkers.rest-api.max-task-pool-size" );
	}

	private void sortFactories(){
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
