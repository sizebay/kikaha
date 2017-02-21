package kikaha.cloud.smart;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import io.undertow.Undertow.Builder;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.modules.Module;
import lombok.extern.slf4j.Slf4j;

/**
 * The smart server module. It will attempt to automatically expose this application
 * to the cloud. The Service Template is defined by the {@link ServiceRegistry} implementation
 * configured on your {@code application.yml}.
 */
@Slf4j
@Singleton
@SuppressWarnings( "unchecked" )
public class SmartServerModule implements Module {

	@Inject Config config;
	@Inject ServiceProvider serviceProvider;
	boolean isModuleEnabled;

	ApplicationData applicationData;
	ServiceRegistry serviceRegistry;
	LocalMachineIdentification localMachineIdentification;

	@PostConstruct
	public void loadApplicationData() throws IOException {
		isModuleEnabled = config.getBoolean( "server.smart-server.enabled" );
		if ( !isModuleEnabled ) return;

		localMachineIdentification = loadLocalMachineIdentification();
		serviceRegistry = loadServiceRegistry();
		applicationData = new ApplicationData(
			() -> localMachineIdentification.generateTheMachineId(),
			() -> localMachineIdentification.getLocalAddress(),
			config.getString( "server.smart-server.application.name" ),
			config.getString( "server.smart-server.application.version" ),
			getLocalPort(), isLocalProtocolHttps()
		);
	}

	ServiceRegistry loadServiceRegistry(){
		final Class<?> serviceRegistryClass = config.getClass( "server.smart-server.service-registry" );
		if ( serviceRegistryClass == null )
			throw new InstantiationError( "No ServiceRegistry defined" );
		return (ServiceRegistry)serviceProvider.load( serviceRegistryClass );
	}

	LocalMachineIdentification loadLocalMachineIdentification(){
		final Class<?> localMachineIdentification = config.getClass( "server.smart-server.local-address.identification" );
		if ( localMachineIdentification == null )
			throw new InstantiationError( "No LocalMachineIdentification defined" );
		return (LocalMachineIdentification)serviceProvider.load( localMachineIdentification );
	}

	public int getLocalPort(){
		if ( isLocalProtocolHttps() )
			return config.getInteger( "server.https.port" );
		else if ( config.getBoolean( "server.http.enabled" ) )
			return config.getInteger( "server.http.port" );
		throw new IllegalArgumentException( "No Http/Https module enabled" );
	}

	public boolean isLocalProtocolHttps() {
		return config.getBoolean( "server.https.enabled" );
	}

	@Override
	public void load( final Builder server, final DeploymentContext context ) throws IOException {
		if ( isModuleEnabled ) {
			log.info("Starting SmartServer cloud module");
			serviceRegistry.registerIntoCluster(applicationData);
		}
	}

	@Override
	public void unload() throws IOException {
		if ( isModuleEnabled ) {
			log.info("Stopping SmartServer cloud module");
			serviceRegistry.deregisterFromCluster(applicationData);
		}
	}

	@Produces
	public ApplicationData produceApplicationData(){
		return applicationData;
	}
}
