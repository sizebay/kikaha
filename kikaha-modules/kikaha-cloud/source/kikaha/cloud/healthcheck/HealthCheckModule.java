package kikaha.cloud.healthcheck;

import java.io.IOException;
import java.util.Collection;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import com.codahale.metrics.health.*;
import io.undertow.Undertow.Builder;
import io.undertow.util.Methods;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;

/**
 * @author: miere.teixeira
 */
@Singleton
public class HealthCheckModule implements Module {

	@Inject Config config;
	@Inject HealthCheckRegistry registry;
	@Inject HealthCheckHttpHandler httpHandler;

	@Inject
	@Typed( HealthCheck.class )
	Collection<HealthCheck> healthChecks;

	@Override
	public void load( final Builder server, final DeploymentContext context ) throws IOException {
		if ( !config.getBoolean( "server.health-check.enabled" ) ) return;

		if ( healthChecks.isEmpty() )
			throw new UnsupportedOperationException( "The health-check module is enabled, but no HealthCheck found on Class Path." );

		deployHealthChecks();
		deployHealthCheckEndpoint( context );
	}

	void deployHealthChecks(){
		int i=0;
		for ( HealthCheck healthCheck : healthChecks ) {
			final Named named = healthCheck.getClass().getAnnotation( Named.class );
			final String healthCheckName = named != null ? named.value() : "health-check" + (i++);
			registry.register( healthCheckName, healthCheck );
		}
	}

	void deployHealthCheckEndpoint( final DeploymentContext context ){
		final String url = config.getString( "server.health-check.url" );
		context.register( url, Methods.GET_STRING, httpHandler );
	}
}
