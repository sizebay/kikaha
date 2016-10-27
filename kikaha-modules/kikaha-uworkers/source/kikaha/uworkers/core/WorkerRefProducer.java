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

	@Inject EndpointContext endpointContext;

	@Produces
	public WorkerRef produceAWorkerRef(ProviderContext providerContext){
		final Worker annotation = providerContext.getAnnotation(Worker.class);
		if ( annotation == null )
			throw new IllegalArgumentException( "Missing @Worker annotation on " + providerContext );
		final EndpointFactory factory = endpointContext.getFactoryFor(annotation.endpoint());
		final WorkerRef workerRef = factory.createWorkerRef(annotation.alias(), annotation.endpoint());
		log.debug( "Creating " + workerRef );
		return workerRef;
	}
}
