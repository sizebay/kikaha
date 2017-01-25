package kikaha.cloud.smart;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.*;
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
	ServiceRegistry serviceRegistry;
	ApplicationData applicationData;
	boolean isModuleEnabled;

	@PostConstruct
	public void loadApplicationData() throws IOException {
		isModuleEnabled = config.getBoolean( "server.smart-server.enabled" );
		if ( !isModuleEnabled ) return;

		final LocalAddressResolver localAddressResolver = loadLocalAddressResolver();
		serviceRegistry = loadServiceRegistry();
		applicationData = new ApplicationData(
			serviceRegistry.generateTheMachineId(),
			config.getString( "server.smart-server.application.name" ),
			config.getString( "server.smart-server.application.version" ),
			localAddressResolver.getLocalAddress(), getLocalPort(), isLocalProtocolHttps()
		);
	}

	private boolean isLocalProtocolHttps() {
		return config.getBoolean( "server.https.enabled" );
	}

	ServiceRegistry loadServiceRegistry(){
		final Class<?> serviceRegistryClass = config.getClass( "server.smart-server.service-registry" );
		if ( serviceRegistryClass == null )
			throw new InstantiationError( "No ServiceRegistry defined" );
		return (ServiceRegistry)serviceProvider.load( serviceRegistryClass );
	}

	LocalAddressResolver loadLocalAddressResolver(){
		final Class<?> localAddressResolver = config.getClass( "server.smart-server.local-address.resolver" );
		if ( localAddressResolver == null )
			throw new InstantiationError( "No LocalAddressResolver defined" );
		return (LocalAddressResolver)serviceProvider.load( localAddressResolver );
	}

	int getLocalPort(){
		if ( config.getBoolean( "server.https.enabled" ) )
			return config.getInteger( "server.https.port" );
		else if ( config.getBoolean( "server.http.enabled" ) )
			return config.getInteger( "server.http.port" );
		throw new IllegalArgumentException( "No Http/Https module enabled" );
	}

	@Override
	public void load( final Builder server, final DeploymentContext context ) throws IOException {
		if ( isModuleEnabled )
			serviceRegistry.registerIntoCluster( applicationData );
	}

	@Override
	public void unload() throws IOException {
		if ( isModuleEnabled )
			serviceRegistry.deregisterFromCluster( applicationData );
	}
}
