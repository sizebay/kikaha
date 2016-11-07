package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;

/**
 * Represents a message listener for Workers' value.
 */
@SuppressWarnings("unchecked")
public interface WorkerEndpointMessageListener {

	/**
	 * Notifies the Worker value of a just received message.
	 *
	 * @param exchange
	 */
	void onMessage( Exchange exchange ) throws Throwable;
}