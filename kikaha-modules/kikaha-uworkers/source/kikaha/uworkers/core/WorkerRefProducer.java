package kikaha.uworkers.core;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import kikaha.core.cdi.ProviderContext;
import kikaha.uworkers.api.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
public class WorkerRefProducer {

	@Inject MicroWorkersContext microWorkersContext;

	@Produces
	public WorkerRef produceAWorkerRef( ProviderContext providerContext ){
		final Worker annotation = providerContext.getAnnotation(Worker.class);
		if ( annotation == null )
			throw new IllegalArgumentException( "Missing @Worker annotation on " + providerContext );

		final EndpointConfig config = microWorkersContext.getEndpointConfig(annotation.value());
		final EndpointFactory factory = config.getEndpointFactory();
		final WorkerRef workerRef = factory.createWorkerRef( config );
		log.debug( "  + Creating " + workerRef );
		return workerRef;
	}
}
