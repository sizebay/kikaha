package kikaha.uworkers.api;

import kikaha.uworkers.core.WrappedExchange;

/**
 * A reference to a worker endpoint.
 */
public interface WorkerRef {

	/**
	 * Send a new request to the Worker referenced by this {@link WorkerRef}.
	 *
	 * @param request
	 * @param <REQ>
	 * @return
	 */
	<REQ> Response send(REQ request);

	/**
	 * Send a new {@link Exchange} to the Worker referenced by this {@link WorkerRef}.
	 * @param exchange
	 * @return
	 */
	Response send( Exchange exchange );

	/**
	 * Forward a received message {@code request} to the Worker referenced by {@link WorkerRef}.
	 * Whoever receive this message will be able to send a reply to the original sender that the
	 * current {@link Exchange} is holding.
	 *
	 * @param exchange
	 * @param request
	 * @param <REQ>
	 */
	default <REQ> void send(Exchange exchange, REQ request ) {
		final Exchange taskExchange = WrappedExchange.wrap(request, exchange);
		send( taskExchange );
	}
}
