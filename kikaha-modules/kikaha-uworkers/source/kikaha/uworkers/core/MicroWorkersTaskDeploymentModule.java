package kikaha.uworkers.core;

import io.undertow.Undertow;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
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

	@Inject EndpointContext endpointContext;

	@Inject
	@Typed( WorkerEndpointMessageListener.class )
	Collection<WorkerEndpointMessageListener> consumers;

	@Override
	public void load( final Undertow.Builder server, final DeploymentContext context) throws IOException {
		if ( consumers.isEmpty() ) return;

		log.info( "Deploying " + consumers.size() + " workers endpoints..." );

		threads = Threads.elasticPool();
		final int defaultParallelism = endpointContext.getEndpointParallelism("default", 1);
		for ( final WorkerEndpointMessageListener consumer : consumers )
			deploy( consumer, defaultParallelism );
	}

	void deploy( final WorkerEndpointMessageListener listener, final int defaultParallelism ) throws IOException {
		final EndpointInboxConsumer consumer = createConsumerFor( listener );
		final int parallelism = endpointContext.getEndpointParallelism( consumer.endpointURL, defaultParallelism );
		runInBackgroundWithParallelism( consumer, parallelism );
	}

	private EndpointInboxConsumer createConsumerFor( final WorkerEndpointMessageListener listener ) throws IOException {
		final Worker annotation = listener.getClass().getAnnotation(Worker.class);
		if ( annotation == null )
			throw new IOException( "Can't instantiate " + listener.getClass() + ": no @Worker value found." );
		final EndpointFactory endpointFactory = endpointContext.getFactoryFor(annotation.value());
		final EndpointInboxSupplier inbox = endpointFactory.createSupplier( annotation.value() );
		return new EndpointInboxConsumer( inbox, listener, annotation.value() );
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
