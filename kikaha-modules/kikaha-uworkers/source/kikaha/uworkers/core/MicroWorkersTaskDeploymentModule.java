package kikaha.uworkers.core;

import io.undertow.Undertow;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.urouting.UndertowHelper;
import kikaha.uworkers.api.Worker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collection;

/**
 *
 */
@Slf4j
@Getter
@Singleton
public class MicroWorkersTaskDeploymentModule implements Module {

	Threads threads;

	@Inject
	@Typed( WorkerEndpointMessageListener.class )
	Collection<WorkerEndpointMessageListener> consumers;

	@Inject MicroWorkersContext microWorkersContext;
	@Inject UndertowHelper undertowHelper;

	@Override
	public void load( final Undertow.Builder server, final DeploymentContext context) throws IOException {
		if ( consumers.isEmpty() ) return;

		log.info( "Deploying " + consumers.size() + " workers endpoints..." );

		threads = Threads.elasticPool();
		final int defaultParallelism = microWorkersContext.getEndpointParallelism("default", 1);
		for ( final WorkerEndpointMessageListener consumer : consumers )
			deploy( context, consumer, defaultParallelism );
	}

	void deploy( final DeploymentContext context, final WorkerEndpointMessageListener listener, final int defaultParallelism ) throws IOException {
		final String listenerName = getListenerName(listener);
		final EndpointFactory endpointFactory = microWorkersContext.getFactoryFor( listenerName );
		EndpointInboxSupplier inbox = endpointFactory.createSupplier( listenerName );
		if ( microWorkersContext.isRestEnabled ) {
			final RESTFulEndpointInboxSupplier restSupplier = RESTFulEndpointInboxSupplier.wrap( inbox, microWorkersContext.maxTaskPoolSize );
			context.register( microWorkersContext.restApiPrefix + "/" + listenerName, "POST", RESTFulMicroWorkersHttpHandler.with( undertowHelper, restSupplier ) );
			inbox = restSupplier;
		}
		final EndpointInboxConsumer consumer = new EndpointInboxConsumer( inbox, listener, listenerName );
		final int parallelism = microWorkersContext.getEndpointParallelism( consumer.name, defaultParallelism );
		runInBackgroundWithParallelism( consumer, parallelism );
	}

	private String getListenerName( final WorkerEndpointMessageListener listener ) throws IOException {
		final Worker annotation = listener.getClass().getAnnotation( Worker.class );
		if ( annotation == null )
			throw new IOException( "Can't instantiate " + listener.getClass() + ": no @Worker value found." );
		return annotation.value();
	}

	void runInBackgroundWithParallelism( final EndpointInboxConsumer consumer, final int parallelism ){
		for ( int i=0; i<parallelism; i++ )
			threads.submit( consumer );
	}

	@Override
	public void unload() {
		threads.shutdown();
	}
}
