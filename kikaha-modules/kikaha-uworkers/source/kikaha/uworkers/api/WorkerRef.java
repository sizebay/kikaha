package kikaha.uworkers.api;

import java.io.IOException;
import kikaha.uworkers.core.WrappedExchange;

/**
 * A reference to a worker value.
 */
public interface WorkerRef {

	/**
	 * Sends an empty object to the Worker.
	 *
	 * @return
	 * @throws IOException
	 */
	default Response send() throws IOException {
		return send( new TimeStamp() );
	}

	/**
	 * Send a new request to the Worker.
	 *
	 * @param request
	 * @param <REQ>
	 * @throws IOException
	 * @return
	 */
	<REQ> Response send(REQ request) throws IOException;

	/**
	 * Send a new {@link Exchange} to the Worker.
	 * @param exchange
	 * @throws IOException
	 * @return
	 */
	Response send( Exchange exchange ) throws IOException;

	/**
	 * Forward a received message {@code request} to the Worker. Whoever receive this message
	 * will be able to send a reply to the original sender that the current {@link Exchange} is holding.
	 *
	 * @param exchange
	 * @param request
	 * @param <REQ>
	 * @throws IOException
	 */
	default <REQ> void send(Exchange exchange, REQ request ) throws IOException {
		final Exchange taskExchange = WrappedExchange.wrap(request, exchange);
		send( taskExchange );
	}
}
