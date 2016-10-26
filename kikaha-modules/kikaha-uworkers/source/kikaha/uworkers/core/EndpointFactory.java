package kikaha.uworkers.core;

import kikaha.uworkers.api.WorkerRef;

/**
 * A factory to instantiate components needed to consume or produce messages
 * that are handled by Workers.
 */
public interface EndpointFactory extends Comparable<EndpointFactory> {

	/**
	 * Returns true if can handle the endpoint {@code endpointName}.
	 *
	 * @param endpointName - the endpoint that uniquely identifies the supplier. If
	 *                     this endpoint is managed by external a broken (ActiveMQ, RabbitMQ,
	 *                     AWS SQS, etc), then the endpoint name could be the endpoint broker URL.
	 * @return
	 */
	boolean canHandleEndpoint( String endpointName );

	/**
	 * Create a {@link EndpointInboxSupplier} for a given {@code endpointName}.
	 * This method will be called automatically by the {@code kikaha-uWorker} module
	 * only if the method {@link EndpointFactory#canHandleEndpoint} have
	 * returned {@code true} for this same {@code endpointName}.
	 *
	 * @param alias - the name used on the configuration file
	 * @param endpointName - the endpoint that uniquely identifies the supplier. If
	 *                     this endpoint is managed by external a broken (ActiveMQ, RabbitMQ,
	 *                     AWS SQS, etc), then the endpoint name could be the endpoint broker URL.
	 * @return
	 */
	EndpointInboxSupplier createSupplier(String alias, String endpointName);

	/**
	 * Creates a reference to a Worker. This method will be called automatically by
	 * the {@code kikaha-uWorker} module only if the method {@link EndpointFactory#canHandleEndpoint} have
	 * returned {@code true} for this same {@code endpointName}.
	 *
	 * @param alias - the name used on the configuration file.
	 * @param endpointName - the endpoint that uniquely identifies the supplier. If
	 *                     this endpoint is managed by external a broken (ActiveMQ, RabbitMQ,
	 *                     AWS SQS, etc), then the endpoint name could be the endpoint broker URL.
	 * @return
	 */
	WorkerRef createWorkerRef(String alias, String endpointName );

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
