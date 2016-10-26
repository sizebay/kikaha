package kikaha.uworkers.core;

import java.io.IOException;
import java.util.*;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import io.undertow.Undertow;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.uworkers.api.Worker;
import lombok.Getter;

/**
 *
 */
@Getter
@Singleton
public class MicroWorkersTaskDeploymentModule implements Module {

	final String name = "uworkers-deployment";
	Threads threads = Threads.elasticPool();

	@Inject EndpointContext endpointContext;

	@Inject
	@Typed( WorkerEndpointMessageListener.class )
	Collection<WorkerEndpointMessageListener> consumers;

	@Override
	public void load( final Undertow.Builder server, final DeploymentContext context) throws IOException {
		final int defaultParallelism = endpointContext.getEndpointParallelism("default", 1);
		for ( final WorkerEndpointMessageListener consumer : consumers )
			deploy( consumer, defaultParallelism );
	}

	void deploy( final WorkerEndpointMessageListener listener, final int defaultParallelism ) throws IOException {
		final EndpointInboxConsumer consumer = createConsumerFor( listener );
		final int parallelism = endpointContext.getEndpointParallelism( consumer.name, defaultParallelism );
		runInBackgroundWithParallelism( consumer, parallelism );
	}

	private EndpointInboxConsumer createConsumerFor( final WorkerEndpointMessageListener listener ) throws IOException {
		final Worker annotation = listener.getClass().getAnnotation(Worker.class);
		if ( annotation == null )
			throw new IOException( "Can't instantiate " + listener.getClass() + ": no @Worker endpoint found." );
		final EndpointFactory endpointFactory = endpointContext.getFactoryFor(annotation.endpoint());
		final EndpointInboxSupplier inbox = endpointFactory.createSupplier( annotation.alias(), annotation.endpoint() );
		return new EndpointInboxConsumer( inbox, listener, annotation.alias(), annotation.endpoint() );
	}

	void runInBackgroundWithParallelism( final EndpointInboxConsumer consumer, final int parallelism ){
		for ( int i=0; i<parallelism; i++ )
			threads.submit( consumer );
	}
}
