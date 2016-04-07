package kikaha.core;

import io.undertow.Undertow;
import kikaha.core.cdi.Application;
import kikaha.core.modules.ModuleLoader;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Getter
@Accessors(fluent = true)
@Singleton
public class KikahaUndertowServer implements Application {

	@Inject
	DeploymentContext deploymentContext;

	@Inject
	ModuleLoader loader;

	Undertow server;

	@Override
	public void run() throws Exception {
		final long start = System.currentTimeMillis();
		Runtime.getRuntime().addShutdownHook( new UndertowShutdownHook() );
		final Undertow.Builder builder = Undertow.builder();
		loader.load( builder, deploymentContext );
		server = builder.build();
		server.start();
		final long elapsed = System.currentTimeMillis() - start;
		log.info( "Server started in " + elapsed + "ms.");
	}

	public void stop() {
		this.server.stop();
		log.info("Server stopped!");
	}

	class UndertowShutdownHook extends Thread {

		@Override
		public void run() {
			KikahaUndertowServer.this.stop();
		}
	}

/*	public void start() throws KikahaException {
		bootstrap();
		this.server = createServer();
		this.server.start();
		reportStartupStatus(elapsed);
	}

	private void reportStartupStatus(final long elapsed) {
		log.info( "Server is listening HTTP at " + configuration.host() + ":" + configuration().port() );
		if ( "HTTPS".equals( mode ) )
			log.info( "Server is also listening HTTPS at " + configuration.secureHost() + ":" + configuration().securePort() );
	}

	protected void bootstrap() throws KikahaException {
		try {
			val deploymentContext = createDeploymentContext();
			runDeploymentHooks(deploymentContext);
			deployWebResourceFolder(deploymentContext);
			this.deploymentContext = deploymentContext;
		} catch (final ServiceProviderException cause) {
			throw new KikahaException(cause);
		}
	}

	protected DefaultDeploymentContext createDeploymentContext()
			throws ServiceProviderException {
		val deploymentHooks = provider.loadAll( DeploymentListener.class );
		return new DefaultDeploymentContext(deploymentHooks);
	}

	protected void runDeploymentHooks(final DeploymentContext deploymentContext) {
		for ( val hook : deploymentContext.deploymentHooks() ) {
			log.debug("Dispatching deployment hook: "
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
		return new ResourceHandler( resourceManager, new NotFoundHandler() )
				.setDirectoryListingEnabled( false )
				.setWelcomeFiles( configuration.welcomeFile() );
	}

	protected File retrieveWebAppFolder() {
		final File location = new File( configuration().resourcesPath() );
		if (!location.exists())
			location.mkdir();
		return location;
	}

	protected Undertow createServer() {
		final Builder builder = Undertow.builder().addHttpListener(
				configuration().port(), configuration().host() );
		appendSSLListenerIfConfigured(builder);
		configureServerOptions(builder);
		final HttpHandler defaultHandler = loadDefaultHttpHandler();
		return builder.setHandler( defaultHandler ).build();
	}

	HttpHandler loadDefaultHttpHandler() {
		HttpHandler defaultHandler = new DefaultHttpRequestHandler( this.deploymentContext() );
		if ( configuration().ssl().autoRedirectFromHttpToHttps() )
			defaultHandler = new AutoHTTPSRedirectHandler(defaultHandler);
		return defaultHandler;
	}

	private void configureServerOptions( final Builder builder ) {
		try {
			val serverOptionsConfiguration = provider.load(DefaultUndertowServerConfiguration.class);
			serverOptionsConfiguration.configure(builder);
		} catch (final ServiceProviderException e) {
			log.error("Can't configure the server. Shutting down...", e);
			System.exit(1);
		}
	}

	private void appendSSLListenerIfConfigured(final Builder builder) {
		val sslContext = readConfiguredSSLContext();
		if ( sslContext != null ) {
			builder.addHttpsListener( configuration().securePort(), configuration().secureHost(), sslContext );
			mode = "HTTPS";
		}
	}

	SSLContext readConfiguredSSLContext() {
		try {
			val factory = provider.load( SSLContextFactory.class );
			return factory.createSSLContext();
		} catch ( IOException | ServiceProviderException cause ) {
			throw new RuntimeException( cause );
		}
	}

	private ServiceProvider newServiceProvider() {
		return new DefaultServiceProvider();
	}

	static <T> List<T> mutableList(final Iterable<T> immutable) {
		val mutableList = new ArrayList<T>();
		for ( val item : immutable )
			mutableList.add(item);
		return mutableList;
	}
	*/
}