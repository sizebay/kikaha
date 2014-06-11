package io.skullabs.undertow.standalone;

import java.util.ArrayList;
import java.util.List;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;
import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.Undertow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class UndertowServer {

	private final ServiceProvider provider = new ServiceProvider();
	private final Configuration configuration;
	private DeploymentContext deploymentContext;
	private Undertow server;

	public void start() throws UndertowStandaloneException {
		long start = System.currentTimeMillis();
		bootstrap();
		this.server = createServer();
		this.server.start();
		long elapsed = System.currentTimeMillis() - start;
		log.info( "Server started in " + elapsed + "ms." );
		log.info( "Server is listening at " + configuration().host() + ":" + configuration().port() );
	}

	protected void bootstrap() throws UndertowStandaloneException {
		try {
			this.deploymentContext = createDeploymentContext();
			doDeploy( this.deploymentContext );
		} catch ( ServiceProviderException cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}

	protected DeploymentContext createDeploymentContext() throws ServiceProviderException {
		Iterable<DeploymentHook> deploymentHooks = provider.loadAll( DeploymentHook.class );
		Iterable<RequestHook> requestHooks = provider.loadAll( RequestHook.class );
		List<RequestHook> mutableListOfHooks = mutableList( requestHooks );
		return new DefaultDeploymentContext( deploymentHooks, mutableListOfHooks );
	}

	static <T> List<T> mutableList( Iterable<T> immutable ) {
		ArrayList<T> mutableList = new ArrayList<T>();
		for ( T item : immutable )
			mutableList.add(item);
		return mutableList;
	}

	protected void doDeploy( DeploymentContext deploymentContext ) {
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
}
