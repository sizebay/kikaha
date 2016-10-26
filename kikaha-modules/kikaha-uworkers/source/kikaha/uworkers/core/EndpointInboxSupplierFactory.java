package kikaha.uworkers.core;

/**
 * Factory interface to provide {@link EndpointInboxSupplier} implementations.
 */
public interface EndpointInboxSupplierFactory extends Comparable<EndpointInboxSupplierFactory> {

	/**
	 * Returns true if can handle the endpoint {@code endpointName}.
	 *
	 * @param endpointName
	 * @return
	 */
	boolean canHandleEndpoint( String endpointName );

	/**
	 * Create a {@link EndpointInboxSupplier} for a given {@code endpointName}.
	 * This method will be called automatically by the {@code kikaha-uWorker} module
	 * only if the method {@link EndpointInboxSupplierFactory#canHandleEndpoint} have
	 * returned {@code true} for this same {@code endpointName}.
	 *
	 * @param endpointName
	 * @return
	 */
	EndpointInboxSupplier createSupplier( String endpointName );

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
	default int compareTo(EndpointInboxSupplierFactory o) {
		return Integer.compare( priority(), o.priority() );
	}
}
