package kikaha.uworkers.api;

import kikaha.uworkers.core.WrappedExchange;

/**
 * A reference to a worker value.
 */
public interface WorkerRef {

	/**
	 * Sends an empty object to the Worker.
	 *
	 * @return
	 */
	default Response send() {
		return send( new TimeStamp() );
	}

	/**
	 * Send a new request to the Worker.
	 *
	 * @param request
	 * @param <REQ>
	 * @return
	 */
	<REQ> Response send(REQ request);

	/**
	 * Send a new {@link Exchange} to the Worker.
	 * @param exchange
	 * @return
	 */
	Response send( Exchange exchange );

	/**
	 * Forward a received message {@code request} to the Worker. Whoever receive this message
	 * will be able to send a reply to the original sender that the current {@link Exchange} is holding.
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
