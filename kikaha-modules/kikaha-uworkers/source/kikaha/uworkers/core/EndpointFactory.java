package kikaha.uworkers.core;

import kikaha.uworkers.api.WorkerRef;

/**
 * A factory to instantiate components needed to consume or produce messages
 * that are handled by Workers.
 */
public interface EndpointFactory extends Comparable<EndpointFactory> {

	/**
	 * Returns true if can handle the value {@code endpointName}.
	 *
	 * @param endpointName - the value that uniquely identifies the supplier
	 * @return
	 */
	boolean canHandleEndpoint( String endpointName );

	/**
	 * Create a {@link EndpointInboxSupplier} for a given {@code endpointName}.
	 * This method will be called automatically by the {@code kikaha-uWorker} module
	 * only if the method {@link EndpointFactory#canHandleEndpoint} have
	 * returned {@code true} for this same {@code endpointName}.
	 *
	 * @param endpointName - the value that uniquely identifies the supplier
	 * @return
	 */
	EndpointInboxSupplier createSupplier(String endpointName);

	/**
	 * Creates a reference to a Worker. This method will be called automatically by
	 * the {@code kikaha-uWorker} module only if the method {@link EndpointFactory#canHandleEndpoint} have
	 * returned {@code true} for this same {@code endpointName}.
	 *
	 * @param endpointName - the value that uniquely identifies the supplier
	 * @return
	 */
	WorkerRef createWorkerRef(String endpointName );

	/**
	 * Defines the priority this factory have. It is useful when we have more than
	 * one factory available on the Class Path. The greater the returned number
	 * higher the priority.
	 *
	 * @return
	 */
	default int priority(){
		return 0;
	}

	/**
	 * Used to compare two factories. Developers are not encouraged to override this method.
	 *
	 * @param o
	 * @return
	 */
	default int compareTo(EndpointFactory o) {
		return Integer.compare( priority(), o.priority() );
	}
}
