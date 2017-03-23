package kikaha.uworkers.core;

import kikaha.uworkers.api.WorkerRef;

/**
 * A factory to instantiate components needed to consume or produce messages
 * that are handled by Workers.
 */
public interface EndpointFactory {

	/**
	 * Create a {@link EndpointInboxSupplier} for a given {@code endpointName}.
	 *
	 * @param config - the object containing all information required to create an {@link EndpointInboxSupplier}
	 * @return
	 */
	EndpointInboxSupplier createSupplier(EndpointConfig config);

	/**
	 * Creates a reference to a Worker.
	 *
	 * @param config - the object containing all information required to create a {@link WorkerRef}
	 * @return
	 */
	WorkerRef createWorkerRef(EndpointConfig config );
}
