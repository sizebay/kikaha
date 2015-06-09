package kikaha.core;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.KikahaException;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.DefaultDeploymentContext;
import kikaha.core.impl.DefaultHttpRequestHandler;
import kikaha.core.ssl.SSLContextFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

import com.typesafe.config.Config;

@Log
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class UndertowServer {

	private final ServiceProvider provider;
	private final Configuration configuration;
	private DeploymentContext deploymentContext;
	private Undertow server;
	volatile String mode = "HTTP";

	public UndertowServer( final Configuration configuration ) {
		this.provider = newServiceProvider();
		this.configuration = configuration;
	}

	/**
	 * Start the Undertow Standalone Server.
	 *
	 * @throws KikahaException
	 */
	public void start() throws KikahaException {
		val start = System.currentTimeMillis();
		Runtime.getRuntime().addShutdownHook( new UndertowShutdownHook(this) );
		bootstrap();
		this.server = createServer();
		this.server.start();
		val elapsed = System.currentTimeMillis() - start;
		reportStartupStatus(elapsed);
	}

	private void reportStartupStatus(final long elapsed) {
		log.info( "Server started in " + elapsed + "ms.");
		log.info( "Server is listening HTTP at " + configuration.host() + ":" + configuration().port() );
		if ( "HTTPS".equals( mode ) )
			log.info( "Server is also listening HTTPS at " + configuration.secureHost() + ":" + configuration().securePort() );
	}

	/**
	 * Run all life cycle initialization routines.
	 *
	 * @throws KikahaException
	 */
	protected void bootstrap() throws KikahaException {
		try {
			provideSomeDependenciesForFurtherInjections();
			val deploymentContext = createDeploymentContext();
			runDeploymentHooks(deploymentContext);
			deployWebResourceFolder(deploymentContext);
			this.deploymentContext = deploymentContext;
		} catch (final ServiceProviderException cause) {
			throw new KikahaException(cause);
		}
	}

	protected void provideSomeDependenciesForFurtherInjections() {
		provider.providerFor( Configuration.class, configuration );
		provider.providerFor( Config.class, configuration().config() );
	}

	protected DefaultDeploymentContext createDeploymentContext()
			throws ServiceProviderException {
		val deploymentHooks = provider.loadAll( DeploymentHook.class );
		return new DefaultDeploymentContext(deploymentHooks);
	}

	protected void runDeploymentHooks(final DeploymentContext deploymentContext) {
		for ( val hook : deploymentContext.deploymentHooks() ) {
			log.fine("Dispatching deployment hook: "
					+ hook.getClass().getCanonicalName());
			hook.onDeploy(deploymentContext);
		}
	}

	protected void deployWebResourceFolder(final DeploymentContext deploymentContext) {
		deploymentContext.fallbackHandler(createResourceManager());
	}

	protected ResourceHandler createResourceManager() {
		val location = retrieveWebAppFolder();
		val resourceManager = new FileResourceManager( location, 100 );
		log.info("Exposing resource files at " + location);
		return Handlers.resource(resourceManager)
				.setResourceManager(resourceManager)
				.setDirectoryListingEnabled( false )
				.setWelcomeFiles( configuration.welcomeFile() );
	}

	protected File retrieveWebAppFolder() {
		val location = new File( configuration().resourcesPath() );
		if (!location.exists())
			location.mkdir();
		return location;
	}

	protected Undertow createServer() {
		val builder = Undertow.builder()
				.addHttpListener( configuration().port(), configuration().host() )
				.setWorkerThreads( 200 );
		val sslContext = readConfiguredSSLContext();
		if ( sslContext != null ) {
			builder.addHttpsListener( configuration().securePort(), configuration().secureHost(), sslContext );
			mode = "HTTPS";
		}
		val defaultHandler = new DefaultHttpRequestHandler( this.deploymentContext() );
		return builder.setHandler( defaultHandler ).build();
	}

	SSLContext readConfiguredSSLContext() {
		try {
			val factory = provider.load( SSLContextFactory.class );
			return factory.createSSLContext();
		} catch ( IOException | ServiceProviderException cause ) {
			throw new RuntimeException( cause );
		}
	}

	public void stop() {
		this.server().stop();
		log.info("Server stopped!");
	}

	private ServiceProvider newServiceProvider() {
		return new ServiceProvider();
	}

	static <T> List<T> mutableList(final Iterable<T> immutable) {
		val mutableList = new ArrayList<T>();
		for ( val item : immutable )
			mutableList.add(item);
		return mutableList;
	}

	@RequiredArgsConstructor
	static class UndertowShutdownHook extends Thread {
		final UndertowServer server;

		@Override
		public void run() {
			server.stop();
		}
	}
}