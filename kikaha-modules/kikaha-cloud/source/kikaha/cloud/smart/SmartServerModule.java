package kikaha.cloud.smart;

import javax.inject.*;
import java.io.IOException;
import io.undertow.Undertow.Builder;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.modules.Module;

/**
 * The smart server module. It will attempt to automatically expose this application
 * to the cloud. The Service Template is defined by the {@link ServiceRegistry} implementation
 * configured on your {@code application.yml}.
 */
@Singleton
@SuppressWarnings( "unchecked" )
public class SmartServerModule implements Module {

	@Inject Config config;
	@Inject ServiceProvider serviceProvider;

	@Override
	public void load( final Builder server, final DeploymentContext context ) throws IOException {
		if ( !config.getBoolean( "server.smart-server.enabled" ) ) return;

		final Class<?> serviceRegistryClass = config.getClass( "server.smart-server.service-registry" );
		if ( serviceRegistryClass == null )
			throw new InstantiationError( "No ServiceRegistry defined" );

		final ServiceRegistry serviceRegistry = (ServiceRegistry)serviceProvider.load( serviceRegistryClass );
		serviceRegistry.registerCluster( new ApplicationData(
			serviceRegistry.generateTheMachineId(),
			config.getString( "server.smart-server.application.name" ),
			config.getString( "server.smart-server.application.version" )
		));
	}
}
