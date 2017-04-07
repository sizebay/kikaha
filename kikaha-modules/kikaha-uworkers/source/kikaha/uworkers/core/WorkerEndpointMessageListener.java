package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;

/**
 * Represents a message listener for Workers' value.
 */
@SuppressWarnings("unchecked")
public interface WorkerEndpointMessageListener {

	/**
	 * Send to the Worker a just received message.
	 *
	 * @param exchange
	 */
	void onMessage( Exchange exchange ) throws Throwable;

	/**
	 * Return the HTTP URI that would allow external access to this endpoint.
	 *
	 * @return
	 */
	default String getHttpEndpoint() {
		return toString();
	}
}