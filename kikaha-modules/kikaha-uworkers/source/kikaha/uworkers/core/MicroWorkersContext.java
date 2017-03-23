package kikaha.uworkers.core;

import kikaha.config.Config;
import kikaha.config.MergeableConfig;
import kikaha.core.cdi.ServiceProvider;
import kikaha.uworkers.core.EndpointConfig.DefaultEndpointConfig;
import lombok.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class MicroWorkersContext {

	@Inject Config config;
	@Inject ServiceProvider cdi;

	@Getter EndpointConfig defaultEndpointConfig;
	@Getter boolean isRestEnabled;
	@Getter String restApiPrefix;
	@Getter int maxTaskPoolSize;

	@PostConstruct
	public void configureEndpointContext(){
		isRestEnabled = config.getBoolean( "server.uworkers.rest-api.enabled" );
		restApiPrefix = config.getString( "server.uworkers.rest-api.base-endpoint" );
		maxTaskPoolSize = config.getInteger( "server.uworkers.rest-api.max-task-pool-size" );
		defaultEndpointConfig = createEndpointConfig( "default" );
	}

	public EndpointConfig getEndpointConfig( String alias ) {
		return createEndpointConfig( alias ).withFallbackTo( defaultEndpointConfig );
	}

	private EndpointConfig createEndpointConfig( String endpointName ) {
		final Config config = getConfigForEndpoint( endpointName );
		final Class<?> endpointFactoryClass = config.getClass( "endpoint-factory" );
		final EndpointFactory endpointFactory = endpointFactoryClass != null ? (EndpointFactory)cdi.load( endpointFactoryClass ) : null;
		return new DefaultEndpointConfig( config, endpointFactory, endpointName );
	}

	private Config getConfigForEndpoint( final String alias ) {
		Config config = this.config.getConfig("server.uworkers." + alias);
		if ( config == null )
			config = MergeableConfig.create();
		return config;
	}
}
