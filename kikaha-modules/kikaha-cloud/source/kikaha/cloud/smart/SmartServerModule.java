package kikaha.cloud.smart;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import io.undertow.Undertow.Builder;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.CDI;
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
	@Inject CDI cdi;

	ApplicationData applicationData;
	ServiceRegistry serviceRegistry;
	MachineIdentification machineIdentification;

	@PostConstruct
	public void loadApplicationData() throws IOException {
		machineIdentification = loadLocalMachineIdentification();
		applicationData = new ApplicationData(
			() -> machineIdentification.generateTheMachineId(),
			() -> machineIdentification.getLocalAddress(),
			config.getString( "server.smart-server.application.name" ),
			config.getString( "server.smart-server.application.version" ),
			getLocalPort(), isLocalProtocolHttps()
		);

		serviceRegistry = loadServiceRegistry();
	}

	ServiceRegistry loadServiceRegistry(){
		final Class<?> serviceRegistryClass = config.getClass( "server.smart-server.service-registry" );
		if ( serviceRegistryClass == null ) {
			log.debug("No ServiceRegistry defined");
			return null;
		}
		return (ServiceRegistry) cdi.load( serviceRegistryClass );
	}

	MachineIdentification loadLocalMachineIdentification(){
		Class<?> localMachineIdentification = config.getClass( "server.smart-server.local-address.identification" );
		if ( localMachineIdentification != null ) {
			log.warn("The entry point 'server.smart-server.local-address.identification' is deprecated.");
			log.warn("Use 'server.smart-server.identification' instead.");
		}
		if ( localMachineIdentification == null )
			localMachineIdentification = config.getClass( "server.smart-server.identification" );
		if ( localMachineIdentification == null )
			throw new InstantiationError( "No MachineIdentification defined" );
		return (MachineIdentification) cdi.load( localMachineIdentification );
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
		if ( serviceRegistry != null ) {
			log.info("Registering application into cluster...");
			serviceRegistry.registerIntoCluster(applicationData);
		}
	}

	@Override
	public void unload() throws IOException {
		if ( serviceRegistry != null ) {
			log.info("Leaving cluster...");
			serviceRegistry.deregisterFromCluster(applicationData);
		}
	}

	@Produces
	public ApplicationData produceApplicationData(){
		return applicationData;
	}
}
