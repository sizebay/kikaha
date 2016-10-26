package kikaha.uworkers.core;

import java.io.IOException;
import java.util.List;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import io.undertow.Undertow;
import kikaha.config.Config;
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

	@Inject
	@Typed( WorkerEndpointMessageListener.class )
	List<WorkerEndpointMessageListener> consumers;

	@Inject
	@Typed( EndpointInboxSupplierFactory.class )
	List<EndpointInboxSupplierFactory> factories;

	@Inject Config config;

	@Override
	public void load( final Undertow.Builder server, final DeploymentContext context) throws IOException {
		final int defaultParallelism = getEndpointParallelism("default", 1);
		for ( final WorkerEndpointMessageListener consumer : consumers )
			deploy( consumer, defaultParallelism );
	}

	void deploy( final WorkerEndpointMessageListener listener, final int defaultParallelism ) throws IOException {
		final EndpointInboxConsumer consumer = createConsumerFor( listener );
		final int parallelism = getEndpointParallelism( consumer.name, defaultParallelism );
		runInBackgroundWithParallelism( consumer, parallelism );
	}

	private EndpointInboxConsumer createConsumerFor( final WorkerEndpointMessageListener listener ) throws IOException {
		final Worker annotation = listener.getClass().getAnnotation(Worker.class);
		if ( annotation == null )
			throw new IOException( "Can't instantiate " + listener.getClass() + ": no @Worker endpoint found." );
		final EndpointInboxSupplier inbox = createSupplierFor( annotation.endpoint() );
		return new EndpointInboxConsumer( inbox, listener, annotation.alias(), annotation.endpoint() );
	}

	EndpointInboxSupplier createSupplierFor(String endpoint ) throws IOException {
		for ( final EndpointInboxSupplierFactory factory : factories )
			if ( factory.canHandleEndpoint( endpoint ) )
				return factory.createSupplier( endpoint );
		throw new IOException( "No EndpointInboxSupplier available for endpoint name: " + endpoint );
	}

	int getEndpointParallelism( final String endpoint, final int defaultParallelism ){
		return config.getInteger("server.uworkers." + endpoint + ".parallelism", defaultParallelism );
	}

	void runInBackgroundWithParallelism( final EndpointInboxConsumer consumer, final int parallelism ){
		for ( int i=0; i<parallelism; i++ )
			threads.submit( consumer );
	}
}
