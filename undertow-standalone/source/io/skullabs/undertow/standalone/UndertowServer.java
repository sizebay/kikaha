package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;
import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@Log
@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class UndertowServer {

	private final ServiceProvider provider = newServiceProvider();
	private final Configuration configuration;
	private DeploymentContext deploymentContext;
	private Undertow server;

	/**
	 * Start the Undertow Standalone Server.
	 * 
	 * @throws UndertowStandaloneException
	 */
	public void start() throws UndertowStandaloneException {
		long start = System.currentTimeMillis();
		bootstrap();
		this.server = createServer();
		this.server.start();
		long elapsed = System.currentTimeMillis() - start;
		log.info( "Server started in " + elapsed + "ms." );
		log.info( "Server is listening at " + configuration().host() + ":" + configuration().port() );
	}

	/**
	 * Run all life cycle initialization routines of Undertow Standalone.
	 *  
	 * @throws UndertowStandaloneException
	 */
	protected void bootstrap() throws UndertowStandaloneException {
		try {
			DefaultDeploymentContext deploymentContext = createDeploymentContext();
			runDeploymentHooks( deploymentContext );
			deployWebResourceFolder( deploymentContext );
			deploymentContext.registerUndertowRoutedResourcesHook();
			this.deploymentContext = deploymentContext;
		} catch ( ServiceProviderException cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}

	protected DefaultDeploymentContext createDeploymentContext() throws ServiceProviderException {
		Iterable<DeploymentHook> deploymentHooks = provider.loadAll( DeploymentHook.class );
		Iterable<RequestHook> requestHooks = provider.loadAll( RequestHook.class );
		List<RequestHook> mutableListOfHooks = mutableList( requestHooks );
		return new DefaultDeploymentContext( deploymentHooks, mutableListOfHooks );
	}

	protected void deployWebResourceFolder( DeploymentContext deploymentContext ) {
		deploymentContext.register( "/", createResourceManager() );
	}

	protected ResourceHandler createResourceManager() {
		File location = retrieveWebAppFolder();
		FileResourceManager resourceManager = new FileResourceManager( location, 100 );
		log.info( "Exposing resource files at " + location );
		return Handlers.resource(resourceManager)
				.setResourceManager( resourceManager )
				.setDirectoryListingEnabled( false );
	}

	protected File retrieveWebAppFolder() {
		File location = new File( configuration().resourcesPath() );
		if ( !location.exists() )
			location.mkdir();
		return location;
	}

	protected void runDeploymentHooks( DeploymentContext deploymentContext ) {
		for ( DeploymentHook hook : deploymentContext.deploymentHooks() ) {
			log.fine( "Dispatching deployment hook: " + hook.getClass().getCanonicalName() );
			hook.onDeploy( deploymentContext );
		}
	}

	protected Undertow createServer() {
		return Undertow.builder()
				.addHttpListener( configuration().port(), configuration().host() )
				.setHandler( new DefaultHttpRequestHandler( this.deploymentContext() ) )
				.build();
	}

	public void stop() {
		this.server().stop();
		log.info( "Server stopped!" );
	}

	private ServiceProvider newServiceProvider() {
		final ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.provideFor(ServiceProvider.class, serviceProvider);
		return serviceProvider;
	}

	static <T> List<T> mutableList( Iterable<T> immutable ) {
		ArrayList<T> mutableList = new ArrayList<T>();
		for ( T item : immutable )
			mutableList.add(item);
		return mutableList;
	}
}
