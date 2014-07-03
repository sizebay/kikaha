package io.skullabs.undertow.standalone;

import com.typesafe.config.Config;
import io.skullabs.undertow.standalone.api.*;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
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
@Accessors(fluent = true)
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
		log.info("Server started in " + elapsed + "ms.");
		log.info("Server is listening at " + host() + ":"
				+ configuration().port());
		Runtime.getRuntime().addShutdownHook( new UndertowShutdownHook(this) );
	}

	/**
	 * Run all life cycle initialization routines of Undertow Standalone.
	 * 
	 * @throws UndertowStandaloneException
	 */
	protected void bootstrap() throws UndertowStandaloneException {
		try {
			provideSomeDependenciesForFurtherInjections();
			DefaultDeploymentContext deploymentContext = createDeploymentContext();
			runDeploymentHooks(deploymentContext);
			deployWebResourceFolder(deploymentContext);
			finishDeployment(deploymentContext);
			this.deploymentContext = deploymentContext;
		} catch (ServiceProviderException cause) {
			throw new UndertowStandaloneException(cause);
		}
	}

	protected void provideSomeDependenciesForFurtherInjections() {
		provider.provideFor(Configuration.class, configuration);
		provider.provideFor(Config.class, configuration().config());
	}

	protected DefaultDeploymentContext createDeploymentContext()
			throws ServiceProviderException {
		Iterable<DeploymentHook> deploymentHooks = provider
				.loadAll(DeploymentHook.class);
		Iterable<RequestHook> requestHooks = provider
				.loadAll(RequestHook.class);
		List<RequestHook> mutableListOfHooks = mutableList(requestHooks);
		return new DefaultDeploymentContext(deploymentHooks, mutableListOfHooks);
	}

	protected void runDeploymentHooks(DeploymentContext deploymentContext) {
		for (DeploymentHook hook : deploymentContext.deploymentHooks()) {
			log.fine("Dispatching deployment hook: "
					+ hook.getClass().getCanonicalName());
			hook.onDeploy(deploymentContext);
		}
	}

	protected void deployWebResourceFolder(DeploymentContext deploymentContext) {
		deploymentContext.fallbackHandler(createResourceManager());
	}

	protected ResourceHandler createResourceManager() {
		File location = retrieveWebAppFolder();
		FileResourceManager resourceManager = new FileResourceManager(location,
				100);
		log.info("Exposing resource files at " + location);
		return Handlers.resource(resourceManager)
				.setResourceManager(resourceManager)
				.setDirectoryListingEnabled(false);
	}

	protected File retrieveWebAppFolder() {
		File location = new File(configuration().resourcesPath());
		if (!location.exists())
			location.mkdir();
		return location;
	}

	protected void finishDeployment(DefaultDeploymentContext deploymentContext) {
		HttpHandler rootHandler = deploymentContext.rootHandler();
		final UndertowRoutedResourcesHook undertowRoutedResources = UndertowRoutedResourcesHook
				.wrap(rootHandler);
		deploymentContext.register(undertowRoutedResources);
	}

	protected Undertow createServer() {
		return Undertow
				.builder()
				.addHttpListener(configuration().port(), host())
				.setHandler(
						new DefaultHttpRequestHandler(this.deploymentContext()))
				.build();
	}

	private String host() {
		String host = configuration().host();
		if (host == "*")
			host = "0.0.0.0";
		return host;
	}

	public void stop() {
		this.server().stop();
		log.info("Server stopped!");
	}

	private ServiceProvider newServiceProvider() {
		final ServiceProvider serviceProvider = new ServiceProvider();
		return serviceProvider;
	}

	static <T> List<T> mutableList(Iterable<T> immutable) {
		final ArrayList<T> mutableList = new ArrayList<T>();
		for (T item : immutable)
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
