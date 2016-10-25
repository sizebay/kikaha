package kikaha.uworkers.core;

/**
 * Factory interface to provide {@link EndpointInboxSupplier} implementations.
 */
public interface EndpointInboxSupplierFactory {

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
}
