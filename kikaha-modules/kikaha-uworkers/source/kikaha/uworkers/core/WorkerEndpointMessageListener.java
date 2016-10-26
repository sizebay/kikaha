package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;

/**
 * Represents a message listener for Workers' endpoint.
 */
@SuppressWarnings("unchecked")
public interface WorkerEndpointMessageListener {

	/**
	 * Notifies the Worker endpoint of a just received message.
	 *
	 * @param exchange
	 */
	void onMessage( Exchange exchange ) throws Throwable;
}